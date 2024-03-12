package de.tomalbrc.bil.file.extra;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import java.util.Map;

public class BbVariablePlaceholders {
    private Map<String, String> values = new Object2ObjectLinkedOpenHashMap<>();

    public BbVariablePlaceholders(String data) {
        var list = data.split("\n");
        for (String entry: list) {
            var split = entry.split("=");
            if (split.length == 2) {
                values.put(split[0].trim(), split[1].trim());
            }
        }
    }

    public String substituteVariables(String expression) {
        for (var entry: values.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue());
        }
        return expression;
    }
}
