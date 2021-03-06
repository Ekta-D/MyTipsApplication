/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mytips.BillingUtils;

import com.android.billingclient.api.BillingClient.SkuType;
import com.mytips.R;

import java.util.ArrayList;

/**
 * Handles Ui specific to "yearly gas" - subscription row
 */
public class GoldYearlyDelegate extends UiManagingDelegate {
        public static final String SKU_ID = "mytipsledger_yearlysub";
  //  public static final String SKU_ID = "com.enjapps.mytips.test";// testing

    public GoldYearlyDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType
    String getType() {
        return SkuType.SUBS;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        if (mBillingProvider.isGoldYearlySubscribed()) {
            holder.button.setText(R.string.no_data_text);
        } else {
            int textId = mBillingProvider.isGoldMonthlySubscribed()
                    ? R.string.folder_id : R.string.folder_id;
            holder.button.setText(textId);
        }
        holder.skuIcon.setImageResource(R.drawable.add_icon);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
        if (data != null) {
            if (mBillingProvider.isGoldMonthlySubscribed()) {
                // If we already subscribed to monthly gas, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                //currentSubscriptionSku.add(GoldMonthlyDelegate.SKU_ID);
                mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }
}
