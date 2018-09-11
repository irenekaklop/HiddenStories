package com.example.eirini.hiddenstories;

import android.app.Application;

import com.estimote.cloud_plugin.common.EstimoteCloudCredentials;
import com.estimote.internal_plugins_api.cloud.CloudCredentials;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {
    public CloudCredentials cloudCredentials =
            new EstimoteCloudCredentials("madgik-fernweh-s-proximity-88y", "d0f830543efbfa65b45a4edea2855eb0");
}
