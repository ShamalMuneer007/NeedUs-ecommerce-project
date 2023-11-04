package com.needus.ecommerce.service.user;

public interface ReferralOfferService {
    void updateOffer(Float referrerAmount, Float refereeAmount);

    Float getReferrerOfferAmount();

    Float getRefereeOfferAmount();
}
