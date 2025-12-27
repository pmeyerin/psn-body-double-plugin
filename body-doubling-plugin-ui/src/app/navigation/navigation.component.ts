import {Component, inject} from '@angular/core';
import {map, Observable, shareReplay} from 'rxjs';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {AsyncPipe} from '@angular/common';
import {MatToolbar} from '@angular/material/toolbar';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-navigation',
  imports: [
    AsyncPipe,
    MatToolbar,
    MatSidenavContent,
    MatSidenav,
    MatSidenavContainer,
    MatIcon
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss',
  standalone: true
})
export class NavigationComponent {
  private readonly breakpointObserver = inject(BreakpointObserver);

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

}
