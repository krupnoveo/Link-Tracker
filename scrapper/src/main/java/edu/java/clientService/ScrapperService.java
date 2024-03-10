package edu.java.clientService;

import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;

public interface ScrapperService {
    GenericResponse<Void> updateLinks(LinkUpdate linkUpdate);
}
