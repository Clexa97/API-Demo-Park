package com.jransani.demoparkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;
    private static final double DESCONTO_PERCENTUAL = 0.30;

    public static String gerarRecibo() {
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring(0, 19);
        return recibo.replaceAll("[-:]", "")
                .replace("T", "-");
    }
    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {

            total = PRIMEIROS_15_MINUTES;

        } else if (minutes <= 60) {

            total = PRIMEIROS_60_MINUTES;

        } else {

            total = PRIMEIROS_60_MINUTES + ( Math.ceil((double) (minutes - 60) / 15)) * ADICIONAL_15_MINUTES;

        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }
    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {

        BigDecimal desconto = BigDecimal.ZERO;

        if ((numeroDeVezes > 0) && numeroDeVezes%10 == 0) {
            BigDecimal percentualDesconto = BigDecimal.valueOf(1-DESCONTO_PERCENTUAL);
            BigDecimal valorDesconto = custo.multiply(percentualDesconto);
            desconto = custo.subtract(valorDesconto);
        }

        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }
}
