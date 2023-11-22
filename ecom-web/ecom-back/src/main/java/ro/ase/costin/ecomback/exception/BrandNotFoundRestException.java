package ro.ase.costin.ecomback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Marca nu a fost găsită")
public class BrandNotFoundRestException extends Exception {

}
