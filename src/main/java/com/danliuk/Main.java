package com.danliuk;

import com.danliuk.model.BusTicket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> tickets = readTicketsFromFile("src/main/resources/ticketData.txt");

            for (String input : tickets) {
                try {
                    BusTicket busTicket = new ObjectMapper().readValue(input, BusTicket.class);

                    String validationResult = validateTicket(busTicket);
                    if (validationResult.equals("VALID")) {
                        System.out.println(busTicket.toString());
                    } else {
                        System.out.println("Неверный билет: " + input + " Причина: " + validationResult);
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Неверный формат билета: " + input);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения билетов из файла: " + e.getMessage());
        }
    }

    private static List<String> readTicketsFromFile(String filePath) throws IOException {
        return Files.readAllLines(new File(filePath).toPath());
    }

    private static String validateTicket(BusTicket busTicket) {
        if (!isStartDateValid(busTicket.getStartDate())) {
            return "Дата начала в будущем или неверный формат даты";
        }
        if (!isTicketTypeValid(busTicket.getTicketType())) {
            return "Неверный тип билета";
        }
        if (!isPriceValid(busTicket.getPrice())) {
            return "Цена должна быть четным числом";
        }
        return "VALID";
    }

    private static boolean isStartDateValid(String startDate) {
        if (startDate == null || startDate.isEmpty()) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isTicketTypeValid(String ticketType) {
        return "DAY".equals(ticketType) || "WEEK".equals(ticketType) ||
                "MONTH".equals(ticketType) || "YEAR".equals(ticketType);
    }

    private static boolean isPriceValid(String price) {
        if (price == null) {
            return false;
        }
        try {
            int priceValue = Integer.parseInt(price);
            return priceValue % 2 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
