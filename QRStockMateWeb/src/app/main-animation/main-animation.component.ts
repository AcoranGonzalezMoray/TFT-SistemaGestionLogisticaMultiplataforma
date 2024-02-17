import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AnimationItem } from 'lottie-web';
import { AnimationOptions } from 'ngx-lottie';

@Component({
  selector: 'app-main-animation',
  templateUrl: './main-animation.component.html',
  styleUrls: ['./main-animation.component.css']
})
export class MainAnimationComponent {

  @Input() ruta!:string;
  @Input() speed!:number;
  @Output() finishEvent = new EventEmitter<void>(); // Evento de salida para notificar al padre

  options!: AnimationOptions;
  animation!: AnimationItem;

  
  ngOnInit(): void {
    this.options = {
      path: this.ruta,
      loop: false
    };
  }

  finish(){
    this.finishEvent.emit();
  }

  animationCreated(animation: AnimationItem): void {
    this.animation = animation;
    this.animation.setSpeed(this.speed); // Puedes ajustar la velocidad aquí, donde 1 es la velocidad normal
  }
}
