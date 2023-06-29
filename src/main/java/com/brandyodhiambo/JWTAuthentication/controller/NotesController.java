package com.brandyodhiambo.JWTAuthentication.controller;


import com.brandyodhiambo.JWTAuthentication.model.Notes;
import com.brandyodhiambo.JWTAuthentication.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.data.domain.Sort.Order;

@RestController
@RequestMapping("/api")
public class NotesController {

    @Autowired
    NotesRepository notesRepository;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping("/sortednotes")
    public ResponseEntity<List<Notes>> getAllNotes(@RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Notes> notes = notesRepository.findAll(Sort.by(orders));

            if (notes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(notes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getAllNotesByPage(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort

    ) {

        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Notes> notes = new ArrayList<Notes>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Notes> pageTuts;
            if (title == null)
                pageTuts = notesRepository.findAll(pagingSort);
            else
                pageTuts = notesRepository.findByTitleContaining(title, pagingSort);

            notes = pageTuts.getContent();

            if (notes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("notes", notes);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
       /* try {
            List<Notes> notes = new ArrayList<Notes>();

            if (title == null) {
                notesRepository.findAll().forEach(notes::add);
            } else {
                notesRepository.findByTitleContaining(title).forEach(notes::add);
            }

            if (notes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(notes, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }*/
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Notes> getNotesById(@PathVariable("id") long id) {
        Optional<Notes> noteData = notesRepository.findById(id);
        return noteData.map(notes -> new ResponseEntity<>(notes, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/notes/published")
    public ResponseEntity<Map<String, Object>> findByPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        try {
            List<Notes> notes = new ArrayList<Notes>();
            Pageable paging = PageRequest.of(page, size);

            Page<Notes> pageTuts = notesRepository.findByPublished(true, paging);
            notes = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("notes", notes);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete_notes")
    public ResponseEntity<HttpStatus> deleteAllNotes() {
        try {
            notesRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete_notes/{id}")
    public ResponseEntity<HttpStatus> deleteNoteById(@PathVariable("id") long id) {
        try {
            notesRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create_note")
    public ResponseEntity<Notes> createNote(@RequestBody Notes notes) {
        try {
            Notes _notes = notesRepository.save(new Notes(notes.getTitle(), notes.getDescription(), notes.isPublished()));
            return new ResponseEntity<>(_notes, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update_note/{id}")
    public ResponseEntity<Notes> updateNotes(@PathVariable("id") long id, @RequestBody Notes notes) {
        Optional<Notes> notesData = notesRepository.findById(id);

        if (notesData.isPresent()) {
            Notes _notes = notesData.get();
            _notes.setTitle(notes.getTitle());
            _notes.setDescription(notes.getDescription());
            _notes.setPublished(notes.isPublished());
            return new ResponseEntity<>(notesRepository.save(_notes), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
