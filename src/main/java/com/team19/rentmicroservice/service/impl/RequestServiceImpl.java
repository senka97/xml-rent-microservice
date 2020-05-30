package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.PriceListAdDTO;
import com.team19.rentmicroservice.dto.RentRequestDTO;
import com.team19.rentmicroservice.dto.RequestCreatedDTO;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;
import com.team19.rentmicroservice.repository.CartItemRepository;
import com.team19.rentmicroservice.repository.RequestRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
            long days = ChronoUnit.DAYS.between(requestAd.getStartDate(),requestAd.getEndDate());
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
}
