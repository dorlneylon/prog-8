package itmo.lab8.server;

import java.net.InetAddress;

public record ClientAddress(InetAddress address, int port) {
}
