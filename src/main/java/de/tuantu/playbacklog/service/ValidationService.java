package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.service.domain.PlaybackLogCsvInputDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.infrastructure.item.validator.Validator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService implements Validator<PlaybackLogCsvInputDto> {

    @Override
    public void validate(PlaybackLogCsvInputDto item) throws ValidationException {
        List<String> violations = new ArrayList<>();
        // TODO: Implement validation business logic

        if (!violations.isEmpty()) {
            throw new ValidationException(String.join("\n", violations));
        }
    }


}
