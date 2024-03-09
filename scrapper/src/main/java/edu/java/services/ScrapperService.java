package edu.java.services;

import edu.java.dto.GenericResponse;
import edu.java.dto.LinkUpdate;

public interface ScrapperService {
    GenericResponse<Void> updateLinks(LinkUpdate linkUpdate);
}
