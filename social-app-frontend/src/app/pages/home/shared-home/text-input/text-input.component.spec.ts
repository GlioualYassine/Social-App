import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { TextInputComponent } from './text-input.component';
import { FriendComponent } from "@home/shared-home/friend/friend.component";
import { PostComponent } from "@home/shared-home/post/post.component";
import { SocialLinkComponent } from "@home/shared-home/social-link/social-link.component";
import { EditDialogComponent } from "@home/shared-home/edit-dialog/edit-dialog.component";
import { CommentListComponent } from "@home/shared-home/comment-list/comment-list.component";
import { CommentComponent } from "@home/shared-home/comment/comment.component";
import { SocialNavbarComponent } from "@home/shared-home/social-navbar/social-navbar.component";
import { CommonModule } from "@angular/common";
import { IconsModule } from "@icons/icons.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatListModule } from "@angular/material/list";
import { SharedAuthModule } from "@auth/shared/shared-auth.module";

@Component({ selector: 'app-test', template: '' })
class TestComponent {}

describe('TextInputComponent', () => {
    let component: TextInputComponent;
    let fixture: ComponentFixture<TextInputComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [
                FriendComponent,
                TextInputComponent,
                PostComponent,
                SocialLinkComponent,
                EditDialogComponent,
                CommentListComponent,
                CommentComponent,
                SocialNavbarComponent,
                TestComponent
            ],
            imports: [
                CommonModule,
                IconsModule,
                FormsModule,
                ReactiveFormsModule,
                MatListModule,
                SharedAuthModule,
            ],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(TextInputComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should bind the windowWidth input correctly', () => {
        component.windowWidth = '800px';
        fixture.detectChanges();

        const inputElement: HTMLInputElement = fixture.nativeElement.querySelector('input');
        expect(inputElement.style.width).toBe('800px');
    });

    it('should update the character counter correctly', () => {
        component.inputText = 'Hello World';
        component.updateCharacterCounter();
        fixture.detectChanges();

        const counterElement: HTMLElement = fixture.nativeElement.querySelector('.character-counter');
        expect(counterElement.textContent).toContain('11');
    });

    it('should return the input size correctly based on the number of characters', () => {
        component.numOfCharacters = 30;
        let inputSize = component.getInputSize();
        expect(inputSize).toBe('2em');

        component.numOfCharacters = 80;
        inputSize = component.getInputSize();
        expect(inputSize).toBe('4em');

        component.numOfCharacters = 120;
        inputSize = component.getInputSize();
        expect(inputSize).toBe('4.5em');
    });
});
