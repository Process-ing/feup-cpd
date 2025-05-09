package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketUtils {
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEYMANAGER_FACTORY_ALG = "SunX509";
    private static final String SSL_CONTEXT_PROTOCOL = "TLS";

    public static Socket newSocket(InetAddress address, int port, String password, String truststorePath) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", truststorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", password);

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket(address, port);

        socket.startHandshake();
        return socket;
    }

    public static SSLServerSocket newServerSocket(int port, char[] password, String keystorePath) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream keyStoreStream = new FileInputStream(keystorePath)) {
            keyStore.load(keyStoreStream, password);
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEYMANAGER_FACTORY_ALG);
        keyManagerFactory.init(keyStore, password);

        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        return serverSocket;
    }
}
