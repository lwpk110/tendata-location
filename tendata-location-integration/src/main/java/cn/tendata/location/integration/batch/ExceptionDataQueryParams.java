package cn.tendata.location.integration.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDataQueryParams {

    private List<Param> exceptionDataParams;

    @Data
    public static class Param{
        private String ipStart;
        private String ipEnd;
    }

}
