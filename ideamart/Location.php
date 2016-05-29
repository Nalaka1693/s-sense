<?php
/**
 *   (C) Copyright 1997-2013 hSenid International (pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid International (pvt) Limited and constitute a TRADE SECRET of hSenid
 *   International (pvt) Limited.
 *
 *   hSenid International (pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 */

include_once 'lib/sms/SmsReceiver.php';
include_once 'lib/sms/SmsSender.php';
include_once 'log.php';

include_once 'lib/simple_html_dom.php';

ini_set('error_log', 'sms-app-error.log');

try {
    $receiver = new SmsReceiver(); // Create the Receiver object

    $content = $receiver->getMessage(); // get the message content
    $address = $receiver->getAddress(); // get the sender's address
    $requestId = $receiver->getRequestID(); // get the request ID
    $applicationId = $receiver->getApplicationId(); // get application ID
    $encoding = $receiver->getEncoding(); // get the encoding value
    $version = $receiver->getVersion(); // get the version

    

    $responseMsg;

    //your logic goes here......
    // $split = explode(' ', $content);
    // $responseMsg = bmiLogicHere($split);
    $responseMsg = outputUSD($address);

    // Create the sender object server url
    $sender = new SmsSender("https://localhost:7443/sms/send");

    //sending a one message

    $applicationId = "APP_000001";
    $encoding = "0";
    $version =  "1.0";
    $password = "password";
    $sourceAddress = "77000";
    $deliveryStatusRequest = "1";
    $charging_amount = ":15.75";
    $destinationAddresses = $address;
    $binary_header = "";

    $res = $sender->sms($responseMsg, $destinationAddresses, $password, $applicationId, $sourceAddress, $deliveryStatusRequest, $charging_amount, $encoding, $version, $binary_header);

} catch (SmsException $ex) {
    //throws when failed sending or receiving the sms
    error_log("ERROR: {$ex->getStatusCode()} | {$ex->getStatusMessage()}");
}


function outputUSD($address){
    $output = "test";
    $url = 'http://localhost:7000/lbs/locate';

    $data = array('applicationId' => 'APP_000001', 'password' => 'password', 'subscriberId' => $address, 'serviceType' => 'IMMEDIATE');


    // Create the context for the request
    $context = stream_context_create(array(
        'http' => array(
            // http://www.php.net/manual/en/context.http.php
            'method' => 'POST',
            'header' => "Authorization: {$authToken}\r\n".
                "Content-Type: application/json\r\n",
            'content' => json_encode($data)
        )
    ));

    // Send the request
    $response = file_get_contents($url, FALSE, $context);

    // Check for errors
    if($response === FALSE){
        die('Error');
    }

    // Decode the response
    $responseData = json_decode($response, TRUE);

    // Print the date from the response
    // echo $responseData['published'];
    $location = (string)$responseData['latitude'].",".(string)$responseData['longitude'];

    $response = file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?latlng='. $location, FALSE);
    $array = json_decode($response);

    return $array;
}


?>