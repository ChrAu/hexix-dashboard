/**
 * A Java API for managing FritzBox HomeAutomation
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.hexix.fritzbox.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * This class creates an {@link SSLSocketFactory} that trusts all certificates.
 *
 * @see #getUnsafeSslSocketFactory()
 */
public class TrustSelfSignedCertificates {

    private TrustSelfSignedCertificates() {
        // Not instantiable
    }

    public static SSLSocketFactory getUnsafeSslSocketFactory() {
        final SSLContext sslContext = getSSLContext("TLS");
        initializeSslContext(sslContext);
        return sslContext.getSocketFactory();
    }

    private static void initializeSslContext(SSLContext sslContext) {
        final KeyManager[] keyManagers = null;
        final TrustManager[] trustManagers = new TrustManager[] { new NullTrustManager() };
        final SecureRandom secureRandom = new SecureRandom();
        final SSLContext sslContext1 = sslContext;
        try {
            sslContext1.init(keyManagers, trustManagers, secureRandom);
        } catch (final KeyManagementException e) {
            throw new HttpException("Error initializing ssl context", e);
        }
    }

    private static SSLContext getSSLContext(String algorithm) {
        try {
            return SSLContext.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new HttpException("Algorithm " + algorithm + " not found", e);
        }
    }
}
