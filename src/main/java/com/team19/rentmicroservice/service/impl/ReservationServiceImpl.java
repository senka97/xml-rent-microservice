package com.team19.rentmicroservice.service.impl;

import com.rent_a_car.rent_service.soap.AddReservationRequest;
import com.rent_a_car.rent_service.soap.AddReservationResponse;
import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.*;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.RequestAd;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.repository.ReservationRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdClient adClient;
    @Autowired
    private RequestAdServiceImpl requestAdService;
    @Autowired
    private RequestServiceImpl requestService;

    @Override
    public Reservation createNewReservation(ReservationDTO reservation, Long ownerID) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        AdDTOSimple ad = this.adClient.getAd(reservation.getAdId(),cp.getPermissions(),cp.getUserID(),cp.getToken());

        if (ad != null)
        {
            // TODO proveriti isto i za zahteve kada bude odradjeno

            Set<Reservation> reservations = findReservationsForThisAd(ad.getId());
            for( Reservation r : reservations)
            {
                if(reservation.getStartDate().equals(r.getStartDate()) || reservation.getStartDate().equals(r.getEndDate())
                        || reservation.getEndDate().equals(r.getStartDate()) || reservation.getEndDate().equals(r.getEndDate()))
                {
                    return  null;
                }

                if(reservation.getStartDate().isAfter(r.getStartDate()))
                {
                    if(reservation.getStartDate().isBefore(r.getEndDate()) )
                    {
                        return null;
                    }
                }
                else if(reservation.getEndDate().isBefore(r.getEndDate()))
                {
                    if(reservation.getEndDate().isAfter(r.getStartDate()))
                    {
                        return null;
                    }
                }
                else if(reservation.getStartDate().isBefore(r.getStartDate()) && reservation.getEndDate().isAfter(r.getEndDate()))
                {
                    return null;
                }

            }

            //proveri se da li postoje zahtevi koji se preklapaju sa novom rezervacijom
            if(requestAdService.checkIfAdReserved(reservation.getAdId(),reservation.getStartDate(),reservation.getEndDate())){
                return null;
            }

            Reservation newReservation = new Reservation();

            newReservation.setClientFirstName(reservation.getClientFirstName());
            newReservation.setClientLastName(reservation.getClientLastName());
            newReservation.setClientEmail(reservation.getClientEmail());
            newReservation.setClientPhoneNumber(reservation.getClientPhoneNumber());
            newReservation.setCurrentPricePerKm(reservation.getCurrentPricePerKm());
            newReservation.setStartDate(reservation.getStartDate());
            newReservation.setEndDate(reservation.getEndDate());
            newReservation.setAdID(ad.getId());
            newReservation.setOwnerID(ownerID);

            PriceListAdDTO pl = adClient.getPriceListForAd(reservation.getAdId(),cp.getPermissions(),cp.getUserID(),cp.getToken());

            double payment = 0;
            long days = ChronoUnit.DAYS.between(reservation.getStartDate(),reservation.getEndDate()) + 1; //plus 1 da bi kad rezervise na jedan dan, cena bila za 1 dan
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
            newReservation.setPayment(payment);

            newReservation = reservationRepository.save(newReservation);

            //automatski se odbiju svi zahtevi koji su Pending i koji se poklapaju sa ovim terminom
            List<Request> requests = requestService.findPendingRequests(reservation.getAdId(),reservation.getStartDate(),reservation.getEndDate());
            if(requests.size() != 0) {
                for (Request r : requests) {
                    r.setStatus(RequestStatus.Canceled);
                    //mozda ovde poslati mejlove da im je odbijen zahtev
                }
                requestService.saveAll(requests);
            }

            return newReservation;
        }
        return null;
    }

    @Override
    public Set<Reservation> findReservationsForThisAd(Long adId) {
        return reservationRepository.findReservationsForThisAd(adId);
    }

    @Override
    public boolean checkIfAdReserved(Long adID, LocalDate startDate, LocalDate endDate) {

        List<Reservation> reservations = this.reservationRepository.findReservations(adID,startDate,endDate);
        if(reservations.size() == 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public List<ReservationFrontDTO> getReservationsFront() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();

        List<ReservationFrontDTO> reservationFrontDTOs = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findReservationsForThisOwner(Long.parseLong(cp.getUserID()));
        if(reservations.size() == 0){
            return reservationFrontDTOs;
        }

        for(Reservation r : reservations)
        {
            ReservationFrontDTO newR = new ReservationFrontDTO();
            newR.setId(r.getId());
            newR.setStartDate(r.getStartDate());
            newR.setEndDate(r.getEndDate());
            newR.setCurrentPricePerKm(r.getCurrentPricePerKm());
            newR.setClientFirstName(r.getClientFirstName());
            newR.setClientLastName(r.getClientLastName());
            newR.setClientEmail(r.getClientEmail());
            newR.setClientPhoneNumber(r.getClientPhoneNumber());
            newR.setPayment(r.getPayment());
            newR.setOwnerID(r.getOwnerID());

            AdFrontDTO ad = new AdFrontDTO();
            ad.setId(r.getAdID());
            newR.setAd(ad);

            reservationFrontDTOs.add(newR);
        }

        List<Long> adIDs = new ArrayList<>(); //ovo su id od oglasa cije podatke moram da uzmem iz ad-microservice
        for(Reservation r: reservations){
            if(!adIDs.contains(r.getAdID())){
                adIDs.add(r.getAdID());
            }
        }

        List<AdFrontDTO> adFrontDTOs = adClient.fillAdsWithInformation(adIDs,cp.getPermissions(),cp.getUserID(),cp.getToken());
        //popunim podacima o oglasu
        for(ReservationFrontDTO r: reservationFrontDTOs){
            AdFrontDTO ad = adFrontDTOs.stream().filter(a -> a.getId() == r.getAd().getId()).findFirst().orElse(null);
            r.setAd(ad);
        }

        return reservationFrontDTOs;
    }

    @Override
    public AddReservationResponse addNewReservationFromAgentApp(AddReservationRequest arr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomPrincipal cp = (CustomPrincipal) auth.getPrincipal();
        AdDTOSimple ad = this.adClient.getAd(arr.getAdMainId(),cp.getPermissions(),cp.getUserID(),cp.getToken());
        if(ad != null){

            Reservation newReservation = new Reservation();
            newReservation.setOwnerID(Long.parseLong(cp.getUserID()));
            newReservation.setAdID(ad.getId());
            newReservation.setClientFirstName(arr.getClientFirstName());
            newReservation.setClientLastName(arr.getClientLastName());
            newReservation.setClientPhoneNumber(arr.getClientPhoneNumber());
            newReservation.setClientEmail(arr.getClientEmail());
            newReservation.setStartDate(LocalDate.parse(arr.getStartDate()));
            newReservation.setEndDate(LocalDate.parse(arr.getEndDate()));
            newReservation.setCurrentPricePerKm(arr.getCurrentPricePerKm());
            newReservation.setPayment(arr.getPayment());
            newReservation = this.reservationRepository.save(newReservation);

            //automatski se odbiju svi zahtevi koji su Pending i koji se poklapaju sa ovim terminom
            List<Request> requests = requestService.findPendingRequests(arr.getAdMainId(),LocalDate.parse(arr.getStartDate()),LocalDate.parse(arr.getEndDate()));
            List<Long> canceledRequests = new ArrayList<>(); //otkazani zahtevi koji se salju na agenta da se i tamo otkazu
            if(requests.size() != 0) {
                for (Request r : requests) {
                    r.setStatus(RequestStatus.Canceled);
                    canceledRequests.add(r.getId());
                    //mozda ovde poslati mejlove da im je odbijen zahtev
                }
                requestService.saveAll(requests);
            }
            AddReservationResponse addReservationResponse = new AddReservationResponse();
            addReservationResponse.setSuccess(true);
            addReservationResponse.setMainId(newReservation.getId());
            addReservationResponse.getCanceledRequests().addAll(canceledRequests);
            return addReservationResponse;

        }else {
            AddReservationResponse addReservationResponse = new AddReservationResponse();
            addReservationResponse.setSuccess(false);
            return addReservationResponse;
        }
    }


}
