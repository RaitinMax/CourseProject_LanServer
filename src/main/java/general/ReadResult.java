package general;

public class ReadResult {

    private final String ip;
    private final int port;

    public ReadResult(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || !obj.getClass().equals(this.getClass())) return false;
        ReadResult readResult = (ReadResult) obj;
        return readResult.getIp().equals(ip) && readResult.getPort() == port;
    }

    @Override
    public String toString() {
        return ip + " " + port;
    }
}
