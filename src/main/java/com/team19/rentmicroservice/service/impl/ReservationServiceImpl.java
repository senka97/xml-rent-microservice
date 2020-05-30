package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.AdDTOSimple;
import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.enums.RequestStatus;
import com.team19.rentmicroservice.model.CartItem;
import com.team19.rentmicroservice.model.Request;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.repository.ReservationRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
}
