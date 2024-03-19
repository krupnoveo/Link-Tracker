package edu.java.httpClientService;

import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import java.util.List;

public interface ScrapperService {
    GenericResponse<Void> notifyChats(List<LinkUpdate> linkUpdate);
}
