package com.plasync.server.controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class ApplicationController extends Controller {
  
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
  
}
