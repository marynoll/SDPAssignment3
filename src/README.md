Mariya Karle
SE-2519

Multi-Channel Notification Gateway

1. What are the two independent dimensions?
    The two independent dimensions are:
        1. The notification type. What we send and how the message is prepared: 
        is it urgent, encrypted, etc.     
2. Where exactly is the bridge?
    The Bridge is the field <p>protected MessageSender sender</p> in the abstract class **Notification**.
    Its type is the interface <p>MessageSender</p>, not a concrete class. Every notification passes the message 
    to its provider only through the interface methods <p>sendMessage, isAvailable and getChannelName</p>,
    so it never knows whether Telegram, Email or SMS is behind it.
3. Why is inheritance problem here?
    With inheritance every combination of type and provider would need its own class:
    <p>UrgentTelegramNotification, UrgentEmailNotification, EncryptedSmsNotification</p>, and so on.
    The number of classes is types x providers. With 4 notification types and 5 providers that is already
    20 classes, and each new type or provider multiplies the number again. The same logic would be 
    copied into many classes. Also, the provider would be fixed by the class of the object, so it could
    not be changed at runtime, and fallback would be hard to build.
4. What happens when a new provider is added?
    If we create one new class that implements <p>MessageSender</p>, for example
    I added <p>PushNotificationSender</p>. None of the existing classes changed, and it works with
    every notification type which is shown by the test <p>pushSenderWorksWithExistingEncryptedNotification</p>

5. What happens when a new Notification type is added?
    We create new subclass for <p>Notification</p>, in my case it is <p>MarketingNotification</p>.
   No provider was changed, and the new type works with all of them (Push, Kafka, Email, and so on), 
    which is shown by the test <p>marketingNotificationWorksWithExistingSenders</p>.
6. Why is this Bridge rather than Adapter?
    Adapter is used after two incompatible interfaces already exist and cannot be changed.
    It is a wrapper that makes one interface look like another for them to be compatible.
    Bridge is used at the design time. We design the interface between them by ourselves, 
    so that both sides can evolve independently.