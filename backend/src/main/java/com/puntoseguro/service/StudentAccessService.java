package com.puntoseguro.service;

import com.puntoseguro.entity.*;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.repository.*;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import java.util.UUID;
import java.util.List;
import com.puntoseguro.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service @RequiredArgsConstructor @Transactional
public class StudentAccessService {
 private final UserRepository users;
 private final StudentRepository students;
 private final StudentLinkRepository links;
 private final TeacherAssignmentRepository assignments;

 public UserEntity current(PuntoSeguroPrincipal principal) {
  return users.findById(principal.getId()).filter(UserEntity::isActivo)
   .filter(u -> u.getRole()==principal.getRole()).orElseThrow(() -> new AccessDeniedException("Acceso denegado"));
 }
 public void centre(PuntoSeguroPrincipal principal) {
  if(current(principal).getRole()!=UserRole.CENTRO) throw new AccessDeniedException("Acceso denegado");
 }
 public boolean canRead(PuntoSeguroPrincipal principal, Integer sid) {
  UserEntity user=current(principal);
  return switch(user.getRole()) {
   case CENTRO -> true;
   case PROFESOR -> assignments.existsByStudentIdAndTeacherId(sid,user.getId());
   case ESTUDIANTE, TUTOR_LEGAL -> links.existsByStudentIdAndUserId(sid,user.getId());
  };
 }
 public void read(PuntoSeguroPrincipal principal, Integer sid) {
  if(!canRead(principal,sid)) throw new AccessDeniedException("Acceso denegado");
 }
 public void write(PuntoSeguroPrincipal principal, Integer sid) {
  UserEntity user=current(principal);
  if(user.getRole()!=UserRole.CENTRO && (user.getRole()!=UserRole.PROFESOR || !assignments.existsByStudentIdAndTeacherId(sid,user.getId()))) throw new AccessDeniedException("Acceso denegado");
 }
 public void link(PuntoSeguroPrincipal principal, Integer sid, UUID uid) {
  centre(principal);
  Student student=students.findById(sid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Estudiante no encontrado"));
  UserEntity user=users.findById(uid).filter(UserEntity::isActivo).filter(u -> u.getRole()==UserRole.ESTUDIANTE || u.getRole()==UserRole.TUTOR_LEGAL).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado"));
  if(links.existsByStudentIdAndUserId(sid,uid)) return;
  if(user.getRole()==UserRole.ESTUDIANTE && links.existsByUserId(uid)) throw new ResponseStatusException(HttpStatus.CONFLICT,"La cuenta de estudiante ya está vinculada");
  StudentLink link=new StudentLink(); link.setStudent(student); link.setUser(user); links.saveAndFlush(link);
 }
 public void unlink(PuntoSeguroPrincipal principal, Integer sid, UUID uid) {
  centre(principal); if(!students.existsById(sid)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Estudiante no encontrado"); links.findByStudentIdAndUserId(sid,uid).ifPresent(links::delete);
 }
 public List<UserResponse> users(PuntoSeguroPrincipal principal) {
  centre(principal);
  return users.findAll().stream().filter(UserEntity::isActivo).map(u -> new UserResponse(u.getId(),u.getNombre(),u.getEmail(),u.getRole())).toList();
 }
 public List<UserResponse> linked(PuntoSeguroPrincipal principal, Integer sid) {
  centre(principal);
  if(!students.existsById(sid)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Estudiante no encontrado");
  return links.findForStudent(sid).stream().map(l -> l.getUser()).map(u -> new UserResponse(u.getId(),u.getNombre(),u.getEmail(),u.getRole())).toList();
 }
 public void changeRole(PuntoSeguroPrincipal principal, UUID uid, UserRole role) {
  centre(principal);
  UserEntity user=users.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado"));
  if(user.getId().equals(principal.getId()) && role!=UserRole.CENTRO) throw new ResponseStatusException(HttpStatus.CONFLICT,"No puedes cambiar tu propio rol de centro");
  if(user.getRole()!=role) { links.deleteByUserId(uid); assignments.deleteByTeacherId(uid); user.setRole(role); user.setSecurityVersion(user.getSecurityVersion()+1); users.saveAndFlush(user); }
 }
}
