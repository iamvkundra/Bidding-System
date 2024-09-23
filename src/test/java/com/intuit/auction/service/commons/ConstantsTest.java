package com.intuit.auction.service.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void testConstants() {
        assertThat(Constants.BASE_URL).isEqualTo("/api/v1/bidding-system");
        assertThat(Constants.AUCTION_SERVICE).isEqualTo(Constants.BASE_URL + "/auction");
        assertThat(Constants.ACCOUNT_SERVICE).isEqualTo(Constants.BASE_URL + "/account");
        assertThat(Constants.BIDDING_SERVICE).isEqualTo(Constants.BASE_URL + "/bidding");
    }
}
