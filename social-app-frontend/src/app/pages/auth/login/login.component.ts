import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { LoginControlProviderService } from "../services/login-control-provider.service";
import { NotFilledDialogComponent } from "../shared/not-filled-dialog/not-filled-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { DialogContents } from "../../../core/enums/DialogContents";
import { DialogService } from "../services/dialog.service";
import { FormBuildingService } from "../services/form-building.service";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
    private dialogListItems !: Array<string>;
    private paragraphContent !: string;
    loginForm !: FormGroup;
    wasSubmitClicked: boolean = false;

    constructor(public controlProvider: LoginControlProviderService,
                private notFilled: MatDialog,
                private dialogService: DialogService,
                private formBuildingService: FormBuildingService) {
    }

    ngOnInit(): void {
        this.loginForm = this.formBuildingService.buildLoginForm();
        this.paragraphContent = DialogContents.LOGIN_PARAGRAPH;
        this.dialogListItems = [DialogContents.LOGIN_EMAIL, DialogContents.LOGIN_PASSWORD];
    }

    authenticateUser(): void {
        if (this.loginForm.invalid) {
            this.wasSubmitClicked = true;

            this.dialogService.openNotFilledDialog(this.notFilled, this.paragraphContent, this.dialogListItems);

            return;
        }
    }
}
