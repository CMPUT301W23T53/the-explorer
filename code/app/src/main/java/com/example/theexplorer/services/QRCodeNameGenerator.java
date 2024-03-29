/**
 * QRCodeNameGenerator is a utility class that generates a name for a QR code.
 */

package com.example.theexplorer.services;

public class QRCodeNameGenerator {

    /**
     * Generates a name for a QR code based on the given hash.
     *
     * @param hash the hash value used to generate the name
     * @return the generated name for the QR code
     */

    public static String generateName(int hash) {
        String[] bit0 = {"cool", "hot"};
        String[] bit1 = {"Monday", "Sunday"};
        String[] bit2 = {"Large", "Small"};
        String[] bit3 = {"Ultra", "Tiny"};
        String[] bit4 = {"Deadly", "Angelic"};
        String[] bit5 = {"Wolf", "Dog"};

        String name = bit0[hash & 1];
        name += bit1[(hash >> 1) & 1];
        name += bit2[(hash >> 2) & 1];
        name += bit3[(hash >> 3) & 1];
        name += bit4[(hash >> 4) & 1];
        name += bit5[(hash >> 5) & 1];

        return name;
    }
}
