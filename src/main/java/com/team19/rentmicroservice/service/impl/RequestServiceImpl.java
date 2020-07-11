package com.team19.rentmicroservice.service.impl;

import com.rent_a_car.rent_service.soap.*;
import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.client.UserClient;
import com.team19.rentmicroservice.dto.*;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.*;
import com.team19.rentmicroservice.rabbitmq.MessageMQ;
import com.team19.rentmicroservice.rabbitmq.Producer;
import com.team19.rentmicroservice.repository.CartItemRepository;
import com.team19.rentmicroservice.repository.RequestAdRepository;
import com.team19.rentmicroservice.repository.RequestRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private AdClient adClient;
    @Autowired
    private ReservationServiceImpl reservationService;
    @Autowired
    private RequestAdServiceImpl requestAdService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private UserInfoServiceImpl userInfoService;
    @Autowired
    private Producer producer;

    Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);


    @Override
    public List<RequestCreatedDTO> createRequests(RentRequestDTO rentRequestDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        Long clientID = Long.parseLong(cp.getUserID());

        List<CartItem> cartItems = cartItemRepository.findCartItems(rentRequestDTO.getCartItemsIDs());
        List<PriceListAdDTO> priceListAdDTOs = new ArrayList<>();
        List<RequestAd> requestAds = new ArrayList<>();
        //od cart items pravim zahteve
        for(CartItem cartItem: cartItems){
            requestAds.add(new RequestAd(cartItem,clientID));
            if(!priceListAdDTOs.stream().filter(p -> p.getAdID() == cartItem.getAdID()).findFirst().isPresent()){
                priceListAdDTOs.add(new PriceListAdDTO(cartItem.getAdID()));
            }
        }
        //dobavim cene za sve pojedinacne zahteve za oglase
        logger.debug("AS-call-S: FP"); //Ad service call start, finding prices
        priceListAdDTOs = adClient.findPrices(priceListAdDTOs,cp.getPermissions(),cp.getUserID(),cp.getToken());
        logger.debug("AS-call-E: FP"); //Ad service call end, finding prices
        //popunim zahteve cenama
        for(RequestAd requestAd: requestAds){
            PriceListAdDTO pl = priceListAdDTOs.stream().filter(p -> p.getAdID() == requestAd.getAdID()).findFirst().orElse(null);
            requestAd.setCurrentPricePerKm(pl.getPricePerKm());
            double payment = 0;
            long days = ChronoUnit.DAYS.between(requestAd.getStartDate(),requestAd.getEndDate()) + 1; //plus 1 da bi kad rezervise na jedan dan, cena bila za 1 dan
            if(days<20){
                payment = days*pl.getPricePerDay();
            }else if(days>=20 && days<30){
                if(pl.getDiscount20Days() > 0) { //ako postoji popust za vise od 20 dana
                    payment = days * pl.getPricePerDay() - (pl.getDiscount20Days() / 100) * (days * pl.getPricePerDay());
                }else{
                    payment = days*pl.getPricePerDay();
                }
            }else{
                if(pl.getDiscount30Days() > 0) { //ako postoji popust za vise od 30 dana
                    payment = days * pl.getPricePerDay() - (pl.getDiscount30Days() / 100) * (days * pl.getPricePerDay());
                }else{ //ako ne postoji za vise od 30, proverim da li postoji za vise od 20 dana
                    if(pl.getDiscount20Days() > 0){
                        payment = days * pl.getPricePerDay() - (pl.getDiscount20Days() / 100) * (days * pl.getPricePerDay());
                    }else{ //ako ne postoji ni za vise od 20 dana
                        payment = days*pl.getPricePerDay();
                    }
                }
            }
            //proverim da li oglas ukljucuje cdw i dodam i cenu za to
            if(pl.isCdwAd()){
                payment += pl.getPriceForCdw();
            }
            requestAd.setPayment(payment);
        }

        //sada se gleda da li je bundle ili ne
        List<RequestCreatedDTO> requestCreatedDTOs = new ArrayList<>();

        if(!rentRequestDTO.isBundle()){ //ako nije svaki se posebno cuva
            for(RequestAd requestAd: requestAds){
                HashSet<RequestAd>  requestAds1 = new HashSet<>();
                requestAds1.add(requestAd);
                Request request = new Request(requestAds1,requestAd.getOwnerID(),requestAd.getClientID());
                requestAd.setRequest(request); //MORAM POSTAVITI NA OBE STRANE
                request = requestRepository.save(request);
                requestCreatedDTOs.add(new RequestCreatedDTO(request));
            }
        }else{ //ako jeste, trazim one koji imaju iste zahteve i grupisem ih
                List<Request> requests = new ArrayList<>();
                for(RequestAd requestAd: requestAds){
                    //proverim da li postoji bundle zahtev sa id od vlasnika
                    Request req = requests.stream().filter(r -> r.getOwnerID() == requestAd.getOwnerID()).findFirst().orElse(null);
                    if(req != null){ //ako postoji trenutni zahtev ubacim u njega
                        req.getRequestAds().add(requestAd);
                        requestAd.setRequest(req); //MORAM POSTAVITI NA OBE STRANE
                    }else{ //ako ne postoji napravi novi zahtev
                        req = new Request(requestAd.getOwnerID(),requestAd.getClientID());
                        req.getRequestAds().add(requestAd);
                        requestAd.setRequest(req); //MORAM POSTAVITI NA OBE STRANE
                        requests.add(req);
                    }
                }
                //sacuvam sve zahteve u bazu
                requests = requestRepository.saveAll(requests);
                for(Request r: requests){
                    requestCreatedDTOs.add(new RequestCreatedDTO(r));
                }
        }

        //izbacim ih iz korpe
        for(CartItem cartItem:cartItems){
            cartItem.setInCart(false);
        }
        cartItemRepository.saveAll(cartItems);

        return requestCreatedDTOs;
    }

    @Override
    public List<Request> findPendingRequests(Long adID, LocalDate startDate, LocalDate endDate) {

        return this.requestRepository.findPendingRequests(adID,startDate,endDate);
    }

    @Override
    public List<Request> saveAll(List<Request> requests) {
        return requestRepository.saveAll(requests);
    }

    @Override
    public Request findOne(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    @Override
    public String acceptRequest(Request request) {

        for(RequestAd ra: request.getRequestAds()){
            //za svaki oglas u zahtevu proverim jos jednom da li se on mozda zauzeo u tom periodu
            //ako jeste odbijem zahtev
            if(this.requestAdService.checkIfAdReserved(ra.getAdID(),ra.getStartDate(),ra.getEndDate()) || this.reservationService.checkIfAdReserved(ra.getAdID(),ra.getStartDate(),ra.getEndDate())){
                request.setStatus(RequestStatus.Canceled);
                requestRepository.save(request);
                return "An ad in this request has been already reserved for the desired period, so this request will be rejected.";
            }
        }
        //ako je doslo dovde znaci da su i dalje svi oglasi slobodni u zeljenom periodu

        //zahtev se odobri i prelazi odmah u paid
        request.setStatus(RequestStatus.Paid);
        requestRepository.save(request);

        //******Slanje mejla*******
        sendEmailForRequestAccept(request);

        // omogucavanje postavljanja komentara i ocenjivanje za svaki zahtev
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        for(RequestAd ra: request.getRequestAds())
        {
            this.adClient.createUserCanPostComment(ra.getAdID(), ra.getClientID(), ra.getEndDate().toString() ,cp.getPermissions(),cp.getUserID(),cp.getToken());
            adClient.createUserCanRate(ra.getClientID(), ra.getAdID(), ra.getEndDate().toString(),
                    cp.getPermissions(), cp.getUserID(), cp.getToken());
        }

        //automatski se odbijaju svi postojeci zahtevi u statusu pending koji se poklapaju sa
        //terminima oglasa u zahtevu koji se odobrava
        for(RequestAd ra: request.getRequestAds()){
            List<Request> requests = requestRepository.findPendingRequests(ra.getAdID(),ra.getStartDate(),ra.getEndDate());
            //sve pronadjene odbijam
            if(requests.size()!=0) {
                for (Request r : requests) {
                    r.setStatus(RequestStatus.Canceled);
                    logger.info(MessageFormat.format("R-ID:{0}-rejected;UserID:{1}", r.getId(), cp.getUserID())); //R-ID:Request id
                    //******Slanje mejla*******
                    sendEmailForRequestReject(r);
                }
                requestRepository.saveAll(requests);
            }
        }

        return null;
    }

    @Override
    public void rejectRequest(Request request) {

        request.setStatus(RequestStatus.Canceled);
        requestRepository.save(request);
        //******Slanje mejla*******
        sendEmailForRequestReject(request);
    }

    @Override
    public void cancelRequest(Request request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        request.setStatus(RequestStatus.Canceled);
        userClient.changeNumberForCanceledRequests(Long.parseLong(cp.getUserID()), cp.getPermissions(), cp.getUserID(), cp.getToken());
        requestRepository.save(request);
    }

    @Override
    public List<RequestFrontDTO> getPendingRequestsFront() {
        //ulogovani korisnik koji trazi zahteve za svoje oglase
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<RequestFrontDTO> requestFrontDTOs = new ArrayList<>();
        List<Request> pendingRequests = this.requestRepository.findAllPendingRequestsForOwner(Long.parseLong(cp.getUserID()));
        if(pendingRequests.size() == 0){
            return requestFrontDTOs;
        }

        requestFrontDTOs = getDataForRequestFrontDTO(requestFrontDTOs, pendingRequests, cp);

        return requestFrontDTOs;
    }

    @Override
    public List<RequestFrontDTO> getPaidRequestsFront(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<RequestFrontDTO> requestFrontDTOs = new ArrayList<>();
        List<Request> paidRequests = this.requestRepository.findAllPaidRequestsForOwner(Long.parseLong(cp.getUserID()));
        if(paidRequests.size() == 0){
            return requestFrontDTOs;
        }

        requestFrontDTOs = getDataForRequestFrontDTO(requestFrontDTOs, paidRequests, cp);

        return requestFrontDTOs;
    }

    @Override
    public List<RequestFrontDTO> getPendingRequestsClientFront(Long clientId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<RequestFrontDTO> requestFrontDTOs = new ArrayList<>();
        List<Request> pendingRequests = this.requestRepository.findAllPendingRequestsForClient(clientId);
        if(pendingRequests.size() == 0){
            return requestFrontDTOs;
        }

        requestFrontDTOs = getDataForRequestFrontDTO(requestFrontDTOs, pendingRequests, cp);
        return requestFrontDTOs;
    }

    @Override
    public List<RequestFrontDTO> getPaidRequestsClientFront(Long clientId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<RequestFrontDTO> requestFrontDTOs = new ArrayList<>();
        List<Request> paidRequests = this.requestRepository.findAllPaidRequestsForClient(clientId);
        if(paidRequests.size() == 0){
            return requestFrontDTOs;
        }

        requestFrontDTOs = getDataForRequestFrontDTO(requestFrontDTOs, paidRequests, cp);

        return requestFrontDTOs;


    }

    public List<RequestFrontDTO> getDataForRequestFrontDTO(List<RequestFrontDTO> requestFrontDTOs, List<Request> requests, CustomPrincipal cp)
    {

        List<ClientFrontDTO> clientFrontDTOs = new ArrayList<>(); //ovo su podaci koje moram da uzmem iz user-microservice
        List<Long> adIDs = new ArrayList<>(); //ovo su id od oglasa cije podatke moram da uzmem iz ad-microservice
        for(Request r: requests){
            requestFrontDTOs.add(new RequestFrontDTO(r));
            if(!clientFrontDTOs.stream().filter(c -> c.getId() == r.getClientID()).findFirst().isPresent()){
                clientFrontDTOs.add(new ClientFrontDTO(r.getClientID()));
            }
            for(RequestAd ra: r.getRequestAds()){
                if(!adIDs.contains(ra.getAdID())){
                    adIDs.add(ra.getAdID());
                }
            }
        }
        //popunila sam informacijama za zahtev i napravila listu klijenata i oglasa cije informacije moram da dobavim
        logger.debug("US-call-S:FC"); //User service call start, FC=fill clients
        clientFrontDTOs = userClient.fillClients(clientFrontDTOs, cp.getToken());
        logger.debug("US-call-E:FC"); //User service call end, FC=fill clients
        //popunim informacijama o klijentima
        for(RequestFrontDTO r: requestFrontDTOs){
            ClientFrontDTO client = clientFrontDTOs.stream().filter(c -> c.getId() == r.getClientID()).findFirst().orElse(null);
            r.setClientLastName(client.getSurname());
            r.setClientName(client.getName());
        }
        //kad sam popunila podacima o klijentu, saljem u ad-microservice da se popune podaci o oglasu
        logger.debug("AS-call-S:FA"); //Ad service call start, FA=fill ads
        List<AdFrontDTO> adFrontDTOs = adClient.fillAdsWithInformation(adIDs,cp.getPermissions(),cp.getUserID(),cp.getToken());
        logger.debug("AS-call-E:FA"); //Ad service call end, FA=fill ads

        //popunim podacima o oglasu
        for(RequestFrontDTO r: requestFrontDTOs){
            for(RequestAdFrontDTO ra: r.getRequestAds()){
                AdFrontDTO ad = adFrontDTOs.stream().filter(a -> a.getId() == ra.getAd().getId()).findFirst().orElse(null);
                ra.setAd(ad);
            }
        }

        return requestFrontDTOs;
    }

    @Override
    public int getPendingRequestsNumber() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        List<Request> pendingRequests = this.requestRepository.findAllPendingRequestsForOwner(Long.parseLong(cp.getUserID()));
        return pendingRequests.size();
    }

    @Override
    public void rejectAllPendingRequestsForBlockedOrRemovedClient(Long id) {
        //odbijaju se svi zahtevi koju su pending ako je obrisan ili blokiran vlasnik oglasa
        List<Request> pendingRequestsForOwner = requestRepository.findAllPendingRequestsForOwner(id);

        if (!pendingRequestsForOwner.isEmpty()) {
            for (Request r : pendingRequestsForOwner) {
                r.setStatus(RequestStatus.Canceled);
                requestRepository.save(r);
            }
        }

        //odbijaju se svi zahtevi koju su pending ako je obrisan ili blokiran korisnik koji je iznajmio oglas
        List<Request> pendingRequestForClient = requestRepository.findAllPendingRequestsForClient(id);
        if (!pendingRequestForClient.isEmpty()) {
            for (Request r : pendingRequestForClient) {
                r.setStatus(RequestStatus.Canceled);
                requestRepository.save(r);
            }
        }
    }

    @Override
    @Scheduled(cron = "${requests.cron}")
    public void rejectPendingRequestsAfter24() {

           System.out.println("Odbijanje zahteva");
           logger.info("R-rejecting(24h) start"); //Requests rejecting start
           List<Request> requests = this.requestRepository.findPendingRequestsAfter24(LocalDateTime.now().minusDays(1));
           System.out.println("Broj zahteva koje treba odbiti: " + requests.size());
           if(requests.size()>0){
               for(Request r: requests){
                   r.setStatus(RequestStatus.Canceled);
                   logger.info(MessageFormat.format("R-ID:{0}-rejected", r.getId()));
                   //******Slanje mejla*******
                   sendEmailForRequestReject(r);
               }
               this.requestRepository.saveAll(requests);
           }
          logger.info("R-rejecting(24h) end"); //Request rejecting end
    }



    @Override
    @Transactional //ovo zbog lazy loading-a, jer se zatvori sesija i ne mogu se procitati requestAds od request
    public GetPendingRResponse findPendingRequestForAgentApp(GetPendingRRequest gpr) {

        //prvo proverim da li su neki zahtevi koji su na agentskoj Pending postali
        //u medjuvremenu na glavnoj Canceled (ako ih je neko otkazao)
        List<Long> canceledIds = new ArrayList<>();
        if(gpr.getIds().size() > 0) { //ako uopste postoje pending zahtevi na agentskoj
            List<Request> canceledRequests = this.requestRepository.checkIfPendingRequestsBecameCanceled(gpr.getIds());
            //ako ima oni koji su otkazani uzmem njihove id
            if (canceledRequests.size() > 0) {
                canceledIds.addAll(canceledRequests.stream().map(Request::getId).collect(Collectors.toList()));
            }
        }
        //Zatim uzmem sve pending koje se ne nalaze u poslatim pending sa agenta i dodam ih
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<Request> pendingRequests = new ArrayList<>();
        if(gpr.getIds().size() > 0) { //ako na agentu postoje pending, pronadji sve pending izuzev njih
            pendingRequests = this.requestRepository.findPendingRequestForAgentApp(gpr.getIds(), Long.parseLong(cp.getUserID()));
        }else{ //ako ne postoje na agentu pending pronadji sve pending za agenta
            pendingRequests = this.requestRepository.findAllPendingRequestsForOwner(Long.parseLong(cp.getUserID()));
        }

        List<RequestSOAP> requestSOAPS = new ArrayList<>();
        for(Request r: pendingRequests){
            RequestSOAP requestSOAP = new RequestSOAP();
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(r.getClientID(),cp.getToken());
            requestSOAP.setId(r.getId());
            requestSOAP.setClientFirstName(userInfoDTO.getName());
            requestSOAP.setClientLastName(userInfoDTO.getSurname());
            requestSOAP.setClientPhoneNumber(userInfoDTO.getPhoneNumber());
            requestSOAP.setClientEmail(userInfoDTO.getEmail());
            for(RequestAd ra: r.getRequestAds()){
                RequestAdSOAP requestAdSOAP = new RequestAdSOAP();
                requestAdSOAP.setId(ra.getId());
                requestAdSOAP.setAdId(ra.getAdID());
                requestAdSOAP.setCurrentPricePerKm(ra.getCurrentPricePerKm());
                requestAdSOAP.setPayment(ra.getPayment());
                requestAdSOAP.setStartDate(ra.getStartDate().toString());
                requestAdSOAP.setEndDate(ra.getEndDate().toString());
                requestSOAP.getRequestAdSOAP().add(requestAdSOAP);
            }
            requestSOAPS.add(requestSOAP);
        }

        System.out.println(requestSOAPS.size());
        GetPendingRResponse getPendingRResponse = new GetPendingRResponse();
        getPendingRResponse.getCanceledRequests().addAll(canceledIds);
        getPendingRResponse.getRequestSOAP().addAll(requestSOAPS);

        return getPendingRResponse;
    }

    @Override
    public RejectPendingRResponse rejectPendingRequestFromAgentApp(RejectPendingRRequest rpr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        logger.debug(MessageFormat.format("R-ID:{0} for rejecting from agent", rpr.getIdMain()));

        Request r = this.requestRepository.findById(rpr.getIdMain()).orElse(null);
        if(r == null){
            logger.warn(MessageFormat.format("R-ID:{0}-NF from agent;UserID:{1}", rpr.getIdMain(), cp.getUserID())); //NF=not found
            RejectPendingRResponse rpResponse = new RejectPendingRResponse();
            rpResponse.setSuccess(false);
            rpResponse.setMessage("Request with that id doesn't exist in the main app. Please contact technical support.");
            return rpResponse;
        }else{
            r.setStatus(RequestStatus.Canceled);
            this.requestRepository.save(r);
            //******Slanje mejla*******
            sendEmailForRequestReject(r);
            logger.info(MessageFormat.format("R-ID:{0}-rejected from agent;UserID:{1}", rpr.getIdMain(), cp.getUserID()));
            RejectPendingRResponse rpResponse = new RejectPendingRResponse();
            rpResponse.setSuccess(true);
            rpResponse.setMessage("Request successfully rejected in the main app.");
            return rpResponse;
        }

    }

    @Override
    @Transactional //isto zbog lazy loading Request.requestAds
    public AcceptPendingRResponse acceptPendingRequestFromAgentApp(AcceptPendingRRequest apr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        Request request = this.requestRepository.findById(apr.getIdMain()).orElse(null);
        System.out.println("Zahtev koji se prihvata: " + apr.getIdMain());
        logger.debug(MessageFormat.format("R-ID:{0} for accepting from agent", apr.getIdMain()));

        if(request == null){
            logger.warn(MessageFormat.format("R-ID:{0}-NF from agent;UserID:{1}", apr.getIdMain(), cp.getUserID())); //NF=not found
            AcceptPendingRResponse apResponse = new AcceptPendingRResponse();
            apResponse.setSuccess(false);
            apResponse.setMessage("Request with that id doesn't exist in the main app. Please contact technical support.");
            return apResponse;
        }else{

            //proverim da li ga je mozda neko otkazao u medjuvremenu
            if(request.getStatus().toString() == "Canceled"){
                System.out.println("Zahtev koji se prihvata je otkazan.");
                logger.info(MessageFormat.format("R-ID:{0}-AC;UserID:{1}", apr.getIdMain(), cp.getUserID())); //AC=already canceled
                AcceptPendingRResponse apResponse = new AcceptPendingRResponse();
                apResponse.setSuccess(false);
                apResponse.setMessage("Request with that id has been canceled.");
                return apResponse;
            }

            //ako nije onda ga prihvatim
            request.setStatus(RequestStatus.Paid);
            requestRepository.save(request);
            //******Slanje mejla*******
            sendEmailForRequestAccept(request);
            logger.info(MessageFormat.format("R-ID:{0}-accepted from agent;UserID:{1}", apr.getIdMain(), cp.getUserID()));

            // omogucavanje postavljanja komentara i ocenjivanje za svaki zahtev
            for(RequestAd ra: request.getRequestAds())
            {
                this.adClient.createUserCanPostComment(ra.getAdID(), ra.getClientID(), ra.getEndDate().toString() ,cp.getPermissions(),cp.getUserID(),cp.getToken());
                adClient.createUserCanRate(ra.getClientID(), ra.getAdID(), ra.getEndDate().toString(),
                        cp.getPermissions(), cp.getUserID(), cp.getToken());
            }

            //automatski se odbijaju svi postojeci zahtevi u statusu pending koji se poklapaju sa
            //terminima oglasa u zahtevu koji se odobrava
            List<Long> canceledRequest = new ArrayList<>(); //zahtevi koji su odbijeni i salju se na agenta da se i tamo odbiju
            for(RequestAd ra: request.getRequestAds()){
                List<Request> requests = requestRepository.findPendingRequests(ra.getAdID(),ra.getStartDate(),ra.getEndDate());
                //sve pronadjene odbijam
                if(requests.size()!=0) {
                    for (Request r : requests) {
                        r.setStatus(RequestStatus.Canceled);
                        canceledRequest.add(r.getId());
                        logger.info(MessageFormat.format("R-ID:{0}-canceled;UserID:{1}", r.getId(),cp.getUserID()));
                        //******Slanje mejla*******
                        sendEmailForRequestReject(r);
                    }
                    requestRepository.saveAll(requests);
                }
            }

            AcceptPendingRResponse apResponse = new AcceptPendingRResponse();
            apResponse.setSuccess(true);
            apResponse.getCanceledRequests().addAll(canceledRequest);
            System.out.println("Odbijenih zahteva je: " + canceledRequest.size());
            apResponse.setMessage("Request successfully accepted in the main app.");
            return apResponse;
        }
    }

    void sendEmailForRequestAccept(Request request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        MessageMQ message = new MessageMQ();
        message.setSubject("Request accepted.");
        String msgStr = "Hello,\n\n";
        for(RequestAd ra : request.getRequestAds()){
            msgStr += "Your request for the ad with id " + ra.getAdID() + " for the period from " + ra.getStartDate().toString() + " to " + ra.getEndDate().toString() + " has been accepted.\n";
        }
        msgStr += "\nBest regards,\nRent-A-Car team";
        message.setContent(msgStr);
        UserInfo userInfo = userInfoService.findUserInfoByUserId((request.getClientID()));
        if(userInfo == null){
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(request.getClientID(),cp.getToken());
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }
        message.setEmail(userInfo.getEmail());
        this.producer.addToMessageQueue("message-queue", message);
    }

    void sendEmailForRequestReject(Request request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        MessageMQ message = new MessageMQ();
        message.setSubject("Request rejected.");
        String msgStr = "Hello,\n\n";
        for(RequestAd ra : request.getRequestAds()){
            msgStr += "Your request for the ad with id " + ra.getAdID() + " for the period from " + ra.getStartDate().toString() + " to " + ra.getEndDate().toString() + " has been rejected.\n";
        }
        msgStr += "\nBest regards,\nRent-A-Car team";
        message.setContent(msgStr);
        UserInfo userInfo = userInfoService.findUserInfoByUserId((request.getClientID()));
        if(userInfo == null){
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(request.getClientID(),cp.getToken());
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }
        message.setEmail(userInfo.getEmail());
        this.producer.addToMessageQueue("message-queue", message);
    }

    void sendEmailForRequestCancel(Request request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        MessageMQ message = new MessageMQ();
        message.setSubject("Request canceled.");
        String msgStr = "Hello,\n\n";
        for(RequestAd ra : request.getRequestAds()){
            msgStr += "Your request for the ad with id " + ra.getAdID() + " for the period from " + ra.getStartDate().toString() + " to " + ra.getEndDate().toString() + " has been canceled.\n";
        }
        msgStr += "\nBest regards,\nRent-A-Car team";
        message.setContent(msgStr);
        UserInfo userInfo = userInfoService.findUserInfoByUserId((request.getOwnerID()));
        if(userInfo == null){
            UserInfoDTO userInfoDTO = this.userClient.getUserInfo(request.getOwnerID(),cp.getToken());
            userInfo = new UserInfo(userInfoDTO);
            userInfo = this.userInfoService.saveUserInfo(userInfo);
        }
        message.setEmail(userInfo.getEmail());
        this.producer.addToMessageQueue("message-queue", message);
    }

}
