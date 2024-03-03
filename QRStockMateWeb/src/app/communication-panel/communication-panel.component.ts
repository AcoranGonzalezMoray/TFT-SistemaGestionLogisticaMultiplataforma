import { Component } from '@angular/core';

@Component({
  selector: 'app-communication-panel',
  templateUrl: './communication-panel.component.html',
  styleUrls: ['./communication-panel.component.css']
})
export class CommunicationPanelComponent {
  isCommunicationClicked: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  onClickCommunicationButton() {
    this.isCommunicationClicked = !this.isCommunicationClicked;
  }
}
