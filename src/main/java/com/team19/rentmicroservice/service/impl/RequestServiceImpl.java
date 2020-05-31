package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.PriceListAdDTO;
import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;
import com.team19.rentmicroservice.repository.CartItemRepository;
import com.team19.rentmicroservice.repository.RequestAdRepository;
import com.team19.rentmicroservice.repository.RequestRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private AdClient adClient;
    @Autowired
    private RequestAdRepository requestAdRepository;


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
        priceListAdDTOs = adClient.findPrices(priceListAdDTOs,cp.getPermissions(),cp.getUserID(),cp.getToken());
        //popunim zahteve cenama
        for(RequestAd requestAd: requestAds){
            PriceListAdDTO pl = priceListAdDTOs.stream().filter(p -> p.getAdID() == requestAd.getAdID()).findFirst().orElse(null);
            requestAd.setCurrentPricePerKm(pl.getPricePerKm());
            double payment = 0;
            long days = ChronoUnit.DAYS.between(requestAd.getStartDate(),requestAd.getEndDate()) + 1; //plus 1 da bi kad rezervise na jedan dan, cena bila za 1 dan
            if(days<20){
                payment = days*pl.getPricePerDay();
            }else if(days>=20 && days<30){
                payment = days*pl.getPricePerDay() - (pl.getDiscount20Days()/100)*(days*pl.getPricePerDay());
            }else{
                payment = days*pl.getPricePerDay() - (pl.getDiscount30Days()/100)*(days*pl.getPricePerDay());
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
            List<RequestAd> requestAds = requestAdRepository.findRequests(ra.getAdID(),ra.getStartDate(),ra.getEndDate());
            if(requestAds.size() != 0){
                request.setStatus(RequestStatus.Canceled);
                requestRepository.save(request);
                return "An ad in this request has been already reserved for the desired period, so this request will be rejected.";
            }
        }
        //ako je doslo dovde znaci da su i dalje svi oglasi slobodni u zeljenom periodu
        //automatski se odbijaju svi postojeci zahtevi u statusu pending koji se poklapaju sa
        //terminima oglasa u zahtevu koji se odobrava
        for(RequestAd ra: request.getRequestAds()){
            List<Request> requests = requestRepository.findPendingRequests(ra.getAdID(),ra.getStartDate(),ra.getEndDate());
            //sve pronadjene odbijam
            if(requests.size()!=0) {
                for (Request r : requests) {
                    r.setStatus(RequestStatus.Canceled);
                }
                requestRepository.saveAll(requests);
                //ovde bi trebalo poslati mejl da su zahtevi odbijeni
            }
        }
        //posle svega ovoga zahtev se odobri i prelazi odmah u paid
        request.setStatus(RequestStatus.Paid);
        requestRepository.save(request);
        return null;
    }

    @Override
    public void rejectRequest(Request request) {

        request.setStatus(RequestStatus.Canceled);
        requestRepository.save(request);
        //poslati mejl za odbijanje
    }

    @Override
    public void cancelRequest(Request request) {

        request.setStatus(RequestStatus.Canceled);
        requestRepository.save(request);
    }

}
