package protocol;

public class ProtocolUtils {
    public static String newLogin(String user, String pass) {
        return String.format("login %s %s", user, pass);
    }

    public static String newRegister(String user, String pass) {
        return String.format("register %s %s", user, pass);
    }

    public static String newLogout() {
        return "logout";
    }

    public static String newEnter(String roomName) {
        return String.format("enter %s", roomName);
    }

    public static String newLeave() {
        return "leave";
    }

    public static String newSend(String message) {
        // TODO(Process-ing): Add vector clock (maybe???)
        return String.format("send %s", message);
    }

    public static String newOk() {
        return "ok";
    }

    public static String newOk(String data) {
        return String.format("ok %s", data);
    }

    public static String newErr(ProtocolErrorIdentifier id) {
        return String.format("err %s", id.getName());
    }
}
