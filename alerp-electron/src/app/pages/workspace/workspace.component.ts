import { Component, OnInit } from "@angular/core";
import { LocalStorageService } from "../../core/services/local-storage.service";
import { AuthService, UserService } from "../../core/services/user.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-workspace',
  templateUrl: './workspace.component.html',
  styleUrls: [ './workspace.component.less' ]
})
export class WorkspaceComponent implements OnInit {

  isCollapsed: boolean = false;
  isOpen: boolean = false;

  constructor(
    private storage: LocalStorageService,
    private user: UserService,
    private auth: AuthService,
    private route: Router
  ) {
  }

  ngOnInit(): void {
  }

  logout(): void {
    this.storage.remove('user');
    this.isOpen = false;
    setTimeout(() => {
      this.route.navigate([ '/passport/login' ]);
    }, 1000);
  }

}
