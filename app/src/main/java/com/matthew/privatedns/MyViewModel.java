package com.matthew.privatedns;
import android.os.AsyncTask;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import org.xbill.DNS.dnssec.ValidatingResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MyViewModel extends ViewModel {
    public final MutableLiveData<String> domain = new MutableLiveData<>("www.fast.com.\n fiber.google.com.\n www.speedtest.net.\n");
    public final MutableLiveData<String> dnsServer = new MutableLiveData<>("8.8.4.4");
    public final ObservableField<String> dnsServe = new ObservableField<>("4.4.4.4");
    public final ObservableField<String> Purl = new ObservableField<>("fast.com.");

    public final ObservableField<String> result = new ObservableField<>("");

    public LiveData<String> getDomain() {
        return domain;
    }

    public void setDomain(String value) {
        domain.setValue(value);
    }

    public LiveData<String> getDnsServer() {
        return dnsServer;
    }

    public void setDnsServer(String value) {
        dnsServer.setValue(value);
    }

    public void onResolver() {
        new DnsResolverTask().execute();
    }
    public ObservableField<String> getInputType() {
        return Purl;
    }
    private class DnsResolverTask extends AsyncTask<Void, Void, String> {
        private List<String> urls = new ArrayList<String>();

        public DnsResolverTask() {
            // Initialize the list and add URLs in the constructor.
            urls.add("www.fast.com.");
            urls.add("fiber.google.com.");
            urls.add("www.speedtest.net.");
        }
        public void updatePurl(String newValue) {
            Purl.set(newValue);
        }
        private String ROOT = ". IN DS 20326 8 2 E06D44B80B8F1D39A95C0B0D7C65D08458E880409BBC683457104237C7F8EC8D";
        @Override
        protected String doInBackground(Void... params) {
            String message = "Standard resolver1: \n";
            try {
//
                SimpleResolver resolver1;
                SimpleResolver resolver2;
                if (Objects.equals(Purl.get(), "www.fast.com.") || Objects.equals(Purl.get(), "fast.com.") || Objects.equals(Purl.get(),"fiber.google.com.") || Objects.equals(Purl.get(),"www.speedtest.net.")) {
                    dnsServe.set("10.122.15.156");
                     resolver2 = new SimpleResolver("10.122.15.156");
//                     resolver1 = new SimpleResolver("10.122.14.158");
                } else {
//                    urls.set(0, Purl.get());
                    dnsServe.set("8.8.8.8");
//                    resolver1 = new SimpleResolver("8.8.4.4");
                     resolver2 = new SimpleResolver("8.8.8.8");
                }
                resolver1 = new SimpleResolver(dnsServe.get());
                message += sendAndPrint(resolver1, Purl.get());
//                message += sendAndPrint(resolver1, urls.get(0));
//                message += sendAndPrint(resolver1, urls.get(1));
//                message += sendAndPrint(resolver1, urls.get(2));
//                dnsServe.set("10.122.14.158");
                System.out.println("Standard resolver1: \n");
//                message += sendAndPrint(resolver2, urls.get(0));
//                message += sendAndPrint(resolver2, urls.get(1));
//                message += sendAndPrint(resolver2, urls.get(2));
            } catch (Exception e) {
                Log.e("Error------------>", e.toString());
                handleGenericError(e);
                message += "\n" + e.getMessage();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            Log.d("Your DNS response message here", message);
            handleDNSResponse(message);
        }

        private String sendAndPrint(Resolver vr, String name) throws IOException, ZoneTransferException, ExecutionException, InterruptedException {
            String result = "\nURL: " + name + "\n";
            Record qr = Record.newRecord(Name.fromConstantString(name), Type.A, DClass.IN);
            Message response = vr.send(Message.newQuery(qr));
            for (RRset set : response.getSectionRRsets(Section.ADDITIONAL)) {
                if (set.getName().equals(Name.root) && set.getType() == Type.TXT
                        && set.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
                    System.out.println("Reason:  " + ((TXTRecord) set.first()).getStrings().get(0));
                    result += "Reason:  " + ((TXTRecord) set.first()).getStrings().get(0) + "\n";
                }
            }

            return response.toString();
        }
    }



    private void handleDNSResponse(String answer) {
        Log.d("DNS Response", answer.toString());
        result.set("DNS Response: \n\n" + answer.toString());
    }

    private void handleDNSResolutionError(Exception ex) {
        Log.e("DNS Resolution Error", ex.toString());
        // Handle the error gracefully, e.g., display an error message to the user
        result.set("DNS Resolution Error: " + ex.toString());
    }

    private void handleGenericError(Exception e) {
        Log.e("Error", e.toString());
        // Handle the error gracefully, e.g., display an error message to the user
        result.set("Error" + e.toString());
    }
}