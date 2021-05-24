package bg.dr.chilly.currencyApi.repository.dtos;


import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FixerIONamesFromJSONImportDTO {

    Boolean success;

    Map<String, String> symbols = new HashMap<>();

}