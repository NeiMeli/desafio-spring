package com.bootcamp.springchallenge.entity.customer;

import com.bootcamp.springchallenge.exception.BadRequestException;

import java.util.Arrays;

public enum Province {
    BUENOS_AIRES("Buenos Aires"),
    CHUBUT("Chubut"),
    CORDOBA("Cordoba"),
    CATAMARCA("Catamarca"),
    SAN_LUIS("San Luis"),
    SANTA_CRUZ("Santa cruz"),
    ENTRE_RIOS("Entre rios"),
    FORMOSA("Formosa"),
    SANTIAGO_DEL_ESTERO("Santiago del estero"),
    CHACO("Chaco"),
    SAN_JUAN("San Juan"),
    LA_PAMPA("La Pampa"),
    JUJUY("Jujuy"),
    RIO_NEGRO("Rio Negro"),
    SALTA("Salta"),
    MENDOZA("Mendoza"),
    TIERRA_DEL_FUEGO("Tierra del fuego"),
    SANTA_FE("Santa Fe"),
    CORRIENTES("Corrientes"),
    MISIONES("Misiones"),
    LA_RIOJA("La Rioja"),
    TUCUMAN("Tucuman"),
    NEUQUEN("Neuquen"),
    UNDEFINED("");

    public String getLabel() {
        return label;
    }

    public static Province fromLabel(String label) {
        String lcLabel = label.toLowerCase();
        return Arrays.stream(values())
                .filter(p -> p.label.toLowerCase().equals(lcLabel))
                .findFirst()
                .orElseThrow(() -> new ProvinceNotFoundException(label));
    }

    private final String label;

    Province(String label) {
        this.label = label;
    }

    public static Province defaultProvince() {
        return UNDEFINED;
    }

    private static class ProvinceNotFoundException extends BadRequestException {
        public ProvinceNotFoundException(String label) {
            super(String.format("No se encontro la provincia %s", label));
        }
    }
}
