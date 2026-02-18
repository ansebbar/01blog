import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SupportService, SupportRequest } from '../../services/support';
import { ReactiveFormsModule, FormsModule } from '@angular/forms'; // Add this


@Component({
  selector: 'app-support',
  templateUrl: './support.html',
  styleUrls: ['./support.css'],
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule]
})
export class Support {
  supportForm: FormGroup;
  isSubmitting = false;
  success = false;
  error = '';

  categories = [
    { value: 'technical', label: 'Technical Issue', icon: 'bi-bug' },
    { value: 'feature', label: 'Feature Request', icon: 'bi-lightbulb' },
    { value: 'account', label: 'Account Help', icon: 'bi-person' },
    { value: 'billing', label: 'Billing Issue', icon: 'bi-credit-card' },
    { value: 'feedback', label: 'Feedback', icon: 'bi-chat' },
    { value: 'other', label: 'Other', icon: 'bi-question-circle' }
  ];

  constructor(
    private fb: FormBuilder,
    private supportService: SupportService
  ) {
    this.supportForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      subject: ['', [Validators.required, Validators.minLength(5)]],
      category: ['', Validators.required],
      message: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]]
    });
  }

  onSubmit() {
    if (this.supportForm.invalid || this.isSubmitting) {
      Object.keys(this.supportForm.controls).forEach(key => {
        const control = this.supportForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;
    this.success = false;
    this.error = '';

    const request: SupportRequest = this.supportForm.value;

    this.supportService.sendSupportRequest(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = true;
          this.supportForm.reset();
          setTimeout(() => {
            this.success = false;
          }, 5000);
        } else {
          this.error = response.message;
        }
        this.isSubmitting = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to send message. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

  resetForm() {
    this.supportForm.reset();
    this.success = false;
    this.error = '';
  }
}