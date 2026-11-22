package com.puntoseguro.config;

import com.puntoseguro.security.PuntoSeguroPrincipal;
import com.puntoseguro.service.StudentAccessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Comprueba el alcance en cada operación de datos académicos. */
@Component @RequiredArgsConstructor
public class StudentAccessInterceptor implements HandlerInterceptor {
 private final StudentAccessService access;
 private static final Pattern STUDENT=Pattern.compile("^/api/students/(\\d+)(?:/(grades|attendance)(?:/\\d+)?)?/?$");
 @Override public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) {
  String path=request.getRequestURI().substring(request.getContextPath().length());
  var auth=SecurityContextHolder.getContext().getAuthentication();
  if(auth==null || !(auth.getPrincipal() instanceof PuntoSeguroPrincipal principal)) return true;
  String method=request.getMethod();
  if(path.equals("/api/students")) { if(!method.equals("GET")) access.centre(principal); else access.current(principal); return true; }
  if(path.startsWith("/api/catalogs/")) { if(!method.equals("GET")) access.centre(principal); else access.current(principal); return true; }
  var match=STUDENT.matcher(path);
  if(match.matches()) {
   int sid=Integer.parseInt(match.group(1));
   if(method.equals("GET")) access.read(principal,sid);
   else if(match.group(2)==null && method.equals("DELETE")) access.centre(principal);
   else access.write(principal,sid);
  }
  return true;
 }
}
