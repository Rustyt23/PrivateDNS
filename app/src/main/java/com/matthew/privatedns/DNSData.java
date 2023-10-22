package com.matthew.privatedns;

import androidx.databinding.ObservableField;

public class DNSData {
    public final ObservableField<String> domain = new ObservableField<>("");
    public final ObservableField<String> dnsServer = new ObservableField<>("");
    public final ObservableField<String> result = new ObservableField<>("");
}
