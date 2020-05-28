package com.team19.rentmicroservice.service.impl;

import com.team19.rentmicroservice.client.AdClient;
import com.team19.rentmicroservice.dto.AdDTOSimple;
import com.team19.rentmicroservice.dto.ReservationDTO;
import com.team19.rentmicroservice.model.Reservation;
import com.team19.rentmicroservice.repository.ReservationRepository;
import com.team19.rentmicroservice.security.CustomPrincipal;
import com.team19.rentmicroservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdClient adClient;

    @Override
    public Reservation createNewReservation(ReservationDTO reservation) {

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

            Reservation newReservation = new Reservation();

            newReservation.setClientFirstName(reservation.getClientFirstName());
            newReservation.setClientLastName(reservation.getClientLastName());
            newReservation.setClientEmail(reservation.getClientEmail());
            newReservation.setClientPhoneNumber(reservation.getClientPhoneNumber());
            newReservation.setCurrentPricePerKm(reservation.getCurrentPricePerKm());
            newReservation.setStartDate(reservation.getStartDate());
            newReservation.setEndDate(reservation.getEndDate());
            newReservation.setAdID(ad.getId());

            return reservationRepository.save(newReservation);

        }
        return null;
    }

    @Override
    public Set<Reservation> findReservationsForThisAd(Long adId) {
        return reservationRepository.findReservationsForThisAd(adId);
    }
}
