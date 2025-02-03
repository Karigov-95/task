package com.example.testthread;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class FileWritingReading {

    private static final String FILE_NAME = "numbers.txt";

    public static void main(String[] args) {
        Thread evenWriter = new Thread(new EvenNumberWriter());
        Thread oddWriter = new Thread(new OddNumberWriter());
        Thread fileReader = new Thread(new FileReaderTask());

        evenWriter.start();
        oddWriter.start();
        fileReader.start();
    }

    static class EvenNumberWriter implements Runnable {
        private final Random random = new Random();
        private final Lock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                writeEvenNumber();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void writeEvenNumber() {
            try {
                int evenNumber = random.nextInt(50) * 2; // Генерируем четное число
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

                    lock.lock();
                    writer.write(evenNumber + "\n");
                    log.info("Написано четное число: {}", evenNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    static class OddNumberWriter implements Runnable {
        private final Random random = new Random();
        private final Lock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                writeOddNumber();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void writeOddNumber() {

            int oddNumber = random.nextInt(50) * 2 + 1;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

                lock.lock();
                writer.write(oddNumber + "\n");
                log.info("Написано нечетное число: {}", oddNumber);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }
    }

    static class FileReaderTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                readLastNumbers();
                try {
                    Thread.sleep(2000); // Задержка для демонстрации чтения
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void readLastNumbers() {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String lastLine = null;
                String line;

                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }
                if (lastLine != null) {
                    System.out.println("Последнее написанное число: " + lastLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
