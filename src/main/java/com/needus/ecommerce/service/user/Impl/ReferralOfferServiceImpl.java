package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.ReferralOffer;
import com.needus.ecommerce.repository.user.ReferralOfferRepository;
import com.needus.ecommerce.service.user.ReferralOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferralOfferServiceImpl implements ReferralOfferService {
    @Autowired
    ReferralOfferRepository referralOfferRepository;

    @Override
    public void updateOffer(Float referrerAmount, Float refereeAmount) {
        referralOfferRepository.save(new ReferralOffer(1L,referrerAmount,refereeAmount));
    }


    @Override
    public Float getReferrerOfferAmount() {
        return referralOfferRepository.findAll().get(0).getReferrerUserPrize();
    }

    @Override
    public Float getRefereeOfferAmount(){return referralOfferRepository.findAll().get(0).getRefereeUserPrize();}
}
