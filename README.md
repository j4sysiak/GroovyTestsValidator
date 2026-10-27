Lekcja 7: Logika Biznesowa, Przetwarzanie Strumieniowe i Testowanie Stanu
Problem: Nasz SatelliteControlCenter na razie tylko generuje raport. W rzeczywistości powinien on podejmować jakieś akcje. Co więcej, przetwarzanie pakietów jeden po drugim jest nieefektywne. Użyjemy bardziej "groovy" podejścia do pracy z kolekcjami.

Zadanie: Zmodyfikujemy nasz system tak, aby:

Grupował pakiety danych według ID satelity.

Dla każdego satelity, podejmował decyzję o wysłaniu komendy (np. ADJUST_ORBIT, REBOOT_SYSTEM, CONTINUE_NORMAL_OPERATION).

Decyzja będzie oparta na analizie wszystkich problemów zgłoszonych dla danego satelity.

Stworzymy nową klasę CommandIssuer, która będzie odpowiedzialna za tę logikę.

Napiszemy solidne testy w Spocku dla CommandIssuer, sprawdzając, czy podejmuje on poprawne decyzje w różnych scenariuszach.
