package veronica.helix;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private boolean waitingForKotekAnswer = false;
    private final String token;

    public UpdateConsumer(@Value("${telegram.bot.token}") String token) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @SneakyThrows
    @Override

    public void consume(Update update) {

        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            System.out.println("Message come from " + update.getMessage().getChatId() + " " + update.getMessage().getText());
            if (waitingForKotekAnswer) {
                hiddenMessageGuessed(messageText, chatId);
                return;
            }
            switch (messageText) {
                case "/start", "!main menu" ->
                        sendMainMenu(chatId);
                case "!spotify" -> sendToUser("The Spotify link is on its way… Stay tuned! 🚀", chatId);
                case "!tik tok" ->
                        sendToUser("Here’s the TikTok link of this year’s best pop artist, Veronica Helix\uD83D\uDC51: https://www.tiktok.com/@veronicahelix", chatId);
                case "!instagram" ->
                        sendToUser("Here's the instagram link of the best pop artist, Veronica Helix\uD83D\uDCA5: https://www.instagram.com/veronicahelix/", chatId);
                case "!threads" ->
                        sendToUser("Here's the threads profile of Veronica Helix, the opening of the upcoming year\uD83C\uDFA4: https://www.threads.com/@veronicahelix", chatId);
                case "!youtube" -> sendToUser("The youtube link is on its way… Stay tuned! 🚀", chatId);
                case "!kotek" -> {
                    waitingForKotekAnswer = true;
                    hiddenMessage("Here we have a small quiz for my kotek!\uD83D\uDC08 \nPlease describe yourself with one word. It is the most powerful one about yourself!\uD83D\uDCA3", chatId, update);
                }
                default -> sendToUser("This command does not exist in this bot, try entering another one!\uD83D\uDE09", chatId);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }


    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        switch (data) {
            case "info" -> sendInformation(chatId);
            case "secret" -> sendSecretInformation(chatId);
        }
    }
    @SneakyThrows
    public void sendSecretInformation(Long chatId){
        String secret = "Hello sunshine!\uD83C\uDF1E Try to write !kotek\uD83D\uDC08";
        SendMessage sendMessageToUser = SendMessage.builder().text(secret).chatId(chatId).build();
        telegramClient.execute(sendMessageToUser);
    }

    @SneakyThrows
    public void sendToUser(String message, Long chatId) {
        SendMessage sendMessageToUser = SendMessage.builder().text(message).chatId(chatId).build();
        telegramClient.execute(sendMessageToUser);
    }

    //    @SneakyThrows
//    public void sendStartMessageVeronica(String message, Long chatId) {
//        InputFile photoVeronica = new InputFile(new File("C:\\Users\\vanya\\Downloads\\startmessage.jpg"));
//        SendPhoto photoStartToSend = new SendPhoto(chatId.toString(), photoVeronica);
//        SendMessage sendMessage = SendMessage.builder().text(message).chatId(chatId).build();
//        photoStartToSend.setCaption(message);
//        telegramClient.execute(photoStartToSend);
////        telegramClient.execute(sendMessage);
//
//    }
    @SneakyThrows
    public void hiddenMessage(String message, Long chatId, Update update) {

        SendMessage sendMessage = SendMessage.builder().text(message).chatId(chatId).build();
        telegramClient.execute(sendMessage);

    }

    @SneakyThrows
    public void hiddenMessageGuessed(String tries, Long chatId) {
        if (tries.equals("best") || tries.equals("Best") || tries.equals("BEST") || tries.equals("THE BEST") || tries.equals("The Best") || tries.equals("The best") || tries.equals("the best")) {
            String message = "Yes my kitten you are the best in the universe!❤\uFE0F The blooming pop artist of the present time!\uD83C\uDFB9 Love you so much❤\uFE0F";
            InputStream imageStream = UpdateConsumer.class.getClassLoader().getResourceAsStream("images/destroyTheWall.jpg");
            if (imageStream == null) {
                throw new RuntimeException("Image not found in resources!");
            }
            InputFile photoVeronica = new InputFile(imageStream, "destroyTheWall.jpg");
            SendPhoto photoStartToSend = new SendPhoto(chatId.toString(), photoVeronica);
            photoStartToSend.setCaption(message);
            telegramClient.execute(photoStartToSend);
            waitingForKotekAnswer = false;
        } else {
            String message = "Hmmm its possible that you are " + tries + " but i have in my mind more specific thing!❤\uFE0F Repeat the attempt my love\uD83D\uDE18";
            SendMessage sendMessage = SendMessage.builder().text(message).chatId(chatId).build();
            telegramClient.execute(sendMessage);
        }
    }

    @SneakyThrows
    public void sendMainMenu(Long chatId) {
        InputStream imageStream = UpdateConsumer.class.getClassLoader().getResourceAsStream("images/img.png");
        if (imageStream == null) {
            throw new RuntimeException("Image not found in resources!");
        }
        InputFile photoVeronica = new InputFile(imageStream, "img.png");
        SendPhoto photoStartToSend = new SendPhoto(chatId.toString(), photoVeronica);
        photoStartToSend.setCaption("🎉 Hey hey! Welcome to the official Telegram bot of Veronica Helix! 🌟 Get ready for 🎶 exclusive updates, behind-the-scenes sneak peeks, and all the hits of the rising pop sensation! 💃🔥");

        var buttonInfo = InlineKeyboardButton.builder()
                .text("Information")
                .callbackData("info")
                .build();
        var buttonSecret = InlineKeyboardButton.builder()
                .text("Small secret")
                .callbackData("secret")
                .build();
        List<InlineKeyboardRow> keyBoardRows = List.of(
                new InlineKeyboardRow(buttonInfo),
                new InlineKeyboardRow(buttonSecret)
        );
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyBoardRows);
        photoStartToSend.setReplyMarkup(markup);
        telegramClient.execute(photoStartToSend);
    }

    @SneakyThrows
    public void sendInformation(Long chatId) {
        String information = "✨ Welcome to the official Telegram bot of Veronica Helix! ✨\n" +
                "\n" +
                "This is the place where you can connect with the world of Veronica Helix \uD83D\uDC83\uD83C\uDFA4 — the rising pop star who’s capturing hearts and setting trends \uD83C\uDF1F.\n" +
                "The main mission of this bot is to make it easier than ever to find information about her \uD83D\uDE80.\n" +
                "\n" +
                "With just a few commands, you can instantly access:\n" +
                "\n" +
                "\uD83D\uDD17 her official social media profiles,\n" +
                "\n" +
                "\uD83C\uDFB6 the latest updates on her music journey,\n" +
                "\n" +
                "✨ exclusive content that brings fans closer together worldwide.\n" +
                "\n" +
                "⚡ Available commands:\n" +
                "\n" +
                "!spotify — the Spotify link is coming soon \uD83C\uDFA7\n" +
                "\n" +
                "!tik tok — watch her new clips and trends on TikTok \uD83C\uDFAC\n" +
                "\n" +
                "!instagram — follow her Instagram for behind-the-scenes vibes \uD83C\uDF38\n" +
                "\n" +
                "!threads — join the conversation on Threads \uD83D\uDD25\n" +
                "\n" +
                "!youtube — subscribe to her YouTube channel for premieres \uD83C\uDFA5\n" +
                "\n" +
                "!main menu — return to the main menu of our bot:) \uD83C\uDF1F\n" +
                "\n" +
                "This bot is your personal guide to everything Veronica Helix \uD83C\uDF0D\uD83D\uDC96.";
        SendMessage sendMessage = SendMessage.builder().text(information).chatId(chatId).build();
        telegramClient.execute(sendMessage);

    }
}
