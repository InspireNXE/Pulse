package org.inspirenxe.pulse.network.pc;

import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.ConnectionListener;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.SessionFactory;

public class PCSessionFactory implements SessionFactory {

    public PCSessionFactory() {}

    @Override
    public Session createClientSession(Client client) {
        // We don't connect to others
        return null;
    }

    @Override
    public ConnectionListener createServerListener(Server server) {
        return new PCConnectionListener(server, server.getHost(), server.getPort());
    }
}
