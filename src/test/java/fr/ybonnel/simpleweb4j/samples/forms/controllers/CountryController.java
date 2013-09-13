package fr.ybonnel.simpleweb4j.samples.forms.controllers;

import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;
import fr.ybonnel.simpleweb4j.samples.forms.model.Countries;

import java.util.List;

/**
 */
public class CountryController {

    public static List<String> getAll(RouteParameters routeParameters) {
        return Countries.list();
    }
}
