package com.example.demo.service;


import com.example.demo.dto.ValuteCursOnDate;
import com.example.demo.entity.ActiveChat;
import com.example.demo.repository.ActiveChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {


    private final CentralRussianBankService centralBankRussianService;
    private final ActiveChatRepository activeChatRepository;
    private final FinanceService financeService;
    private Map<Long, List<String>> previousCommands = new ConcurrentHashMap<>();
    @Value("${bot.api.key}") //Сюда будет вставлено значение из application.properties, в котором будет указан api key, полученный от BotFather
    private String apiKey;

    @Value("${bot.name}") //Как будут звать нашего бота
    private String name;
    //Это основной метод, который связан с обработкой сообщений

    private static final String CURRENT_RATES = "/currentrates";
    private static final String ADD_INCOME = "/addincome";
    private static final String ADD_SPEND = "/addspend";

    //Данный метод будет вызван сразу после того, как данный бин будет создан - это обеспечено аннотацией Spring PostConstruct
    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", name, apiKey);
    }

    //Данный метод просто возвращает данные о имени бота и его необходимо переопределять
    @Override
    public String getBotUsername() {
        return name;
    }
    //Данный метод возвращает API ключ для взаимодействия с Telegram
    @Override
    public String getBotToken() {
        return apiKey;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage(); //Этой строчкой мы получаем сообщение от пользователя
        Long chatId = message.getChatId();
        try {
            SendMessage response = new SendMessage(); //Данный класс представляет собой реализацию команды отправки сообщения, которую за нас выполнит ранее подключенная библиотека
             //ID чата, в который необходимо отправить ответ
            response.setChatId(String.valueOf(chatId)); //Устанавливаем ID, полученный из предыдущего этап сюда, чтобы сообщить, в какой чат необходимо отправить сообщение

            //Тут начинается самое интересное - мы сравниваем, что прислал пользователь, и какие команды мы можем обработать. Пока что у нас только одна команда
            if (CURRENT_RATES.equalsIgnoreCase(message.getText())) {

                List<ValuteCursOnDate> listCurrencies = this.centralBankRussianService.getCurrenciesFromCbr();

                String responseText = "";
//Получаем все курсы валют на текущий момент и проходимся по ним в цикле
                for (ValuteCursOnDate valuteCursOnDate : listCurrencies) {
                    responseText = StringUtils.defaultIfBlank(response.getText(), "");
                    System.out.println("Iteration: " + responseText + "\n");
//В данной строчке мы собираем наше текстовое сообщение
//StringUtils.defaultBlank – это метод из библиотеки Apache Commons, который нам нужен для того, чтобы на первой итерации нашего цикла была вставлена пустая строка вместо null, а на следующих итерациях не перетерся текст, полученный из предыдущих итерации. Подключение библиотеки см. ниже
                    response.setText(responseText + valuteCursOnDate.getName() + " - " + valuteCursOnDate.getCourse() + "\n");
                }


            }   else if (ADD_INCOME.equalsIgnoreCase(message.getText())) {
                putPreviousCommand(chatId, message.getText());
            response.setText("Отправьте мне сумму полученного дохода");
        } else if (ADD_SPEND.equalsIgnoreCase(message.getText())) {
                putPreviousCommand(chatId, message.getText());
            response.setText("Отправьте мне сумму расходов");
        } else {
            response.setText(financeService.addFinanceOperation(getPreviousCommand(message.getChatId()), message.getText(), message.getChatId()));
        }

            execute(response);


            //Теперь мы сообщаем, что пора бы и ответ отправлять

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (activeChatRepository.findActiveChatByChatId(chatId).isEmpty()) {
                ActiveChat activeChat = new ActiveChat();
                activeChat.setChatId(chatId);
                activeChatRepository.save(activeChat);
            }
        }


    }


    public void sendNotificationToAllActiveChats(String message, Set<Long> chatIds) {
        for (Long id : chatIds) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(id));
            sendMessage.setText(message);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void putPreviousCommand(Long chatId, String command) {
        if (previousCommands.get(chatId) == null) {
            List<String> commands = new ArrayList<>();
            commands.add(command);
            previousCommands.put(chatId, commands);
        } else {
            previousCommands.get(chatId).add(command);
        }
    }

    private String getPreviousCommand(Long chatId) {
        return previousCommands.get(chatId)
                .get(previousCommands.get(chatId).size() - 1);
    }
}
