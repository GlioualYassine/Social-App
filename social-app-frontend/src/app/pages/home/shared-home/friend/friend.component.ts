import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { UtilService } from "@services/utils/util.service";
import { UserHomeDataService } from "@core/services/home/user-home-data.service";
import { HomeApiCalls } from "@core/enums/HomeApiCalls";
import { Subject, takeUntil } from "rxjs";

@Component({
    selector: 'app-friend',
    templateUrl: './friend.component.html',
    styleUrls: ['./friend.component.scss']
})
export class FriendComponent implements OnDestroy {
    private destroyFriend$: Subject<any> = new Subject<any>();
    @Input() friendUsername !: string;
    @Input() friendName!: string;
    @Input() friendJob!: string;
    @Output() friendRemoval: EventEmitter<string> = new EventEmitter<string>();

    constructor(private utilService: UtilService,
                private userDataService: UserHomeDataService) {
    }

    removeUserFromFriends(): void {
        this.userDataService.manageUsersFriendEndpoints(this.friendUsername, HomeApiCalls.REMOVE_FRIEND)
            .pipe(takeUntil(this.destroyFriend$))
            .subscribe((): void => {
                this.friendRemoval.emit(this.friendUsername);
            });
    }

    ngOnDestroy(): void {
        this.destroyFriend$.complete();
    }
}
