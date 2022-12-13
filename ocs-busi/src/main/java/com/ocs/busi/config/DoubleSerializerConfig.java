package com.ocs.busi.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ocs.common.core.text.Convert;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonComponent
public class DoubleSerializerConfig extends JsonSerializer<Object> {

    /**
     * 序列化操作,继承JsonSerializer，重写Serialize函数
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(new BigDecimal(Convert.toDouble(value)).setScale(2, RoundingMode.HALF_UP).toString());
    }
}
