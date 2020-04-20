package com.suai;

public class  MD4 {

    private static final int A = 0x67452301;
    private static final int B = (int) 0xEFCDAB89L;
    private static final int C = (int) 0x98BADCFEL;
    private static final int D = 0x10325476;
    private static final int[] for_cycle3 = {0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15};

    private static final int[] S = { 3, 7, 11, 19,
            3, 5, 9, 13,
            3, 9, 11, 15
    };

    public byte[] hashMD4(byte[] message) {
        //вычисляем длину сообщения
        int messageBytes = message.length;
        int numBlocks = ((messageBytes + 8) >>> 6) + 1;
        int totalLen = numBlocks * 64;
        //записываем в добавочный массив на первое место 1
        byte[] paddingBytes = new byte[totalLen - messageBytes];
        paddingBytes[0] = (byte) 0x80;

        long messageBits = (long) messageBytes * 8;
        for (int i = 0; i < 8; i++) {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageBits;
            messageBits >>>= 8;
        }
        //инициализируем копии слов а б с д
        int a = A;
        int b = B;
        int c = C;
        int d = D;
        int[] buffer = new int[16]; //будет обрабатывать текст по 64 байта
        for (int i = 0; i < numBlocks; i++) {
            int index = i << 6;
            for (int j = 0; j < 64; j++, index++) //в этом месте записываем добавочные символы и длину в текстовый массив
                buffer[j >>> 2] = ((int) ((index < messageBytes) ? message[index] : paddingBytes[index - messageBytes]) << 24) | (buffer[j >>> 2] >>> 8);
            int AA = a;
            int BB = b;
            int CC = c;
            int DD = d;
            //начинаем обрабатывать входное сообщение блоками по 16 слов
            for (int j = 0; j < 48; j++) {
                int div16 = j >>> 4; //в каком "цикле" сейчас находимся
                int f = 0, temp = 0;
                int bufferIndex = j % 16; //какой фрагмент в выбранном буфере текста берем
                switch (div16) {
                    case 0: //ф-ия F - выполняется 16 раз (как и другие оставшиеся)
                        f = (b & c) | (~b & d);
                        temp = Integer.rotateLeft(a + f + buffer[bufferIndex], S[(div16 * 4) | (j % 4)]);
                        break;

                    case 1: //ф-ия G
                        f = (b & c) | (b & d) | (c & d);
                        bufferIndex = 4 * (bufferIndex % 4) + (bufferIndex / 4);
                        temp = Integer.rotateLeft(a + f + buffer[bufferIndex] + (int) 0x5A827999, S[(div16 * 4) | (j % 4)]);
                        break;

                    case 2: //ф-ия H
                        f = b ^ c ^ d;
                        temp = Integer.rotateLeft(a + f + buffer[for_cycle3[bufferIndex]] + (int) 0x6ED9EBA1, S[(div16 * 4) | (j % 4)]);
                        break;
                }
                //сдвигаем получившиеся слова (перезаписываем)
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            //прибавляем к полученным словам исходные
            a += AA;
            b += BB;
            c += CC;
            d += DD;
        }
        //запись хеша в массив
        byte[] md = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            //из какого слова записывать
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++) {
                md[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md;
    }
    //метод для вывода хеша
    public String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }
}