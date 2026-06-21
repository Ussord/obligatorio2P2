/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package modelo.enums;

/**
 *
 * @author Mauro
 */
public enum CategoriaPeso {
    MENOR_A_1_KG,
    DESDE_1_HASTA_MENOS_5_KG,
    DESDE_5_HASTA_MENOS_10_KG,
    DE_10_KG_O_MAS;

    public static CategoriaPeso desdeGramos(int pesoGramos) {
        if (pesoGramos <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero.");
        }

        if (pesoGramos < 1000) {
            return MENOR_A_1_KG;
        }

        if (pesoGramos < 5000) {
            return DESDE_1_HASTA_MENOS_5_KG;
        }

        if (pesoGramos < 10000) {
            return DESDE_5_HASTA_MENOS_10_KG;
        }

        return DE_10_KG_O_MAS;
    }
}
