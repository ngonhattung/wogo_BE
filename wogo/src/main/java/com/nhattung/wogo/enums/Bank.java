package com.nhattung.wogo.enums;

public enum Bank {
    VIETCOMBANK("Vietcombank"),
    TECHCOMBANK("Techcombank"),
    BIDV("BIDV"),
    AGRIBANK("Agribank"),
    VPBANK("VPBank"),
    ACB("ACB"),
    SACOMBANK("Sacombank"),
    MBBANK("MBBank"),
    EXIMBANK("Eximbank"),
    OCB("OCB"),
    TPBANK("TPBank"),
    HDBANK("HDBank"),
    SHB("SHB"),
    SEAABANK("SeaABank"),
    ABBANK("ABBANK"),
    IVB("IVB"),
    NAMABANK("Nam A Bank"),
    PGBANK("PGBank"),
    SCB("SCB"),
    VIB("VIB"),
    MSBANK("MSBank");

    private final String displayName;

    Bank(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
