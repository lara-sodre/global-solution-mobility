package br.com.fiap.ms.avaliacoes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("ms-locais")
public interface LocalClient {

    @RequestMapping(method = RequestMethod.PUT,
            value = ("/api/v1/locais/{localId}/avaliacao/validada"))
    void confirmarAvaliacao(@PathVariable("localId") Long localId);
}
