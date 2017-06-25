package ru.nsu.ccfit.saltanova.messages;


public interface ObserverMessage {          //rename

    void process(ServerTextMessage message);

    void process(RequestListSuccess message);

    void process(RequestListError message);

    void process(LoginSuccess message);

    void process(LoginError message);

    void process(TextMessageError message);

    void process(TextMessageSuccess message);

    void process(UserLoginMessage message);

    void process(UserLogoutMessage message);

    void process(LogoutSuccess message);

    void process(LogoutError logoutError);
}
