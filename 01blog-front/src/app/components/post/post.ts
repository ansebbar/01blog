import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../services/post-service';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { UpdatePost } from '../../models/update-post';
// import { marked } from 'marked';

@Component({
  selector: 'app-post-presentation',
  templateUrl: './post.html',
  styleUrls: ['./post.css'],
  standalone: true,
  imports: [CommonModule, FormsModule,RouterLink],
})
export class PostPresentationComponent implements OnInit {
  @Input() post!: any;
  
  isLiked: boolean = false;
  isDisliked: boolean = false;
  isBookmarked: boolean = false;
  isLoading: boolean = true;
  reported: boolean = false;
  newComment: string = '';
  reportReason: string = '';
  showFullContent: boolean = false;
  showReadMore: boolean = false;
  renderedContent: SafeHtml = '';
  currentUser: string = localStorage.getItem('username') || '';

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private postService: PostService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const postId = params['id'];
      this.loadPost(postId);
    });
  }

  loadPost(postId: number) {
    this.isLoading = true;
    this.postService.getPostById(postId).subscribe({
      next: (post) => {
        this.post = post;
        console.log('Loaded post:', this.post);
        this.isLiked = this.post.likedByCurrentUser;
        this.isDisliked = this.post.dislikedByCurrentUser;
        // this.isBookmarked = localStorage.getItem(`post_${this.post.id}_bookmarked`) === 'true';
        this.renderMarkdown(this.post.content);
        this.showReadMore = this.post.content?.length > 500;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading post:', error);
        this.isLoading = false;
      }
    });
  }

  getCategoryColor(category: string): string {
    const colors: {[key: string]: string} = {
      'Technology': 'primary',
      'Lifestyle': 'success',
      'Business': 'warning',
      'Entertainment': 'info',
      'Education': 'danger',
      'Health': 'dark'
    };
    return colors[category] || 'secondary';
  }

  toggleFullContent(event: Event) {
    event.stopPropagation();
    this.showFullContent = !this.showFullContent;
  }

  navigateToPost() {
    this.router.navigate(['/posts', this.post.id]);
  }

  toggleLike(event: Event) {
    event.stopPropagation();
    console.log('Toggling like for post:', this.post.id, 'isLiked:', this.isLiked);
    this.isLiked = !this.isLiked;
    this.post.likes = (this.post.likes || 0) + (this.isLiked ? 1 : -1);
    
    // const username = localStorage.getItem('username') || '';
    if (this.isLiked) {
      if (!this.post.postLikesusers) this.post.postLikesusers = [];
      this.post.postLikesusers.push(this.currentUser);
    } else {
      const index = this.post.postLikesusers?.indexOf(this.currentUser);
      if (index > -1) this.post.postLikesusers.splice(index, 1);
    }
    //todoo: send like status to backend
    const updatePost: UpdatePost = {
      type: "like",
      comment: "",
      like: this.isLiked,
      username: this.currentUser,
      title: "",
      content: "",
      categories: [],
      visibility: ""
    };
    this.postService.updatePost(this.post.id, updatePost).subscribe({
       next: (post) => {
        console.log("new post",post)
        this.post = post;
        this.isLiked = this.post.likedByCurrentUser;
        this.isDisliked = this.post.dislikedByCurrentUser;
       },
       error: (err) => {
        console.log(err);
       }
      }
    );
  }

  toggleDislike(event: Event) {
    event.stopPropagation();
    console.log('Toggling dislike for post:', this.post.id, 'isDisliked:', this.isDisliked);
    this.isDisliked = !this.isDisliked;
    this.post.dislikes = (this.post.dislikes || 0) + (this.isDisliked ? 1 : -1);
    
    // const username = localStorage.getItem('username') || '';
    if (this.isDisliked) {
      if (!this.post.postDislikesusers) this.post.postDislikesusers = [];
      this.post.postDislikesusers.push(this.currentUser);
    } else {
      const index = this.post.postDislikesusers?.indexOf(this.currentUser);
      if (index > -1) this.post.postDislikesusers.splice(index, 1);
    }
    //todoo: send like status to backend
    const updatePost: UpdatePost = {
      type: "dislike",
      comment: "",
      like: this.isDisliked,
      username: this.currentUser,
      title: "",
      content: "",
      categories: [],
      visibility: ""
    };
    this.postService.updatePost(this.post.id, updatePost).subscribe({
       next: (post) => {
        console.log("new post",post)
        this.post = post;
        this.isLiked = this.post.likedByCurrentUser;
        this.isDisliked = this.post.dislikedByCurrentUser;
       },
       error: (err) => {
        console.log(err);
       }
      });
  }

reportPost() {
this.postService.reportPost(this.post.id, this.reportReason).subscribe({next: (message) => { 
  alert(message);
  // console.log('Post reported successfully');
  this.reported = false;
}, error: (error) => { 
  console.error('Error reporting post:', error); 
} }); }

  addComment() {
    if (this.newComment.trim()) {
      if (!this.post.comments) this.post.comments = [];
      this.post.comments.push({
        creator: this.currentUser,
        content: this.newComment.trim(),
        avatarurl: localStorage.getItem('userProfile') || '',
        date: new Date().toISOString()
      });
      
      const updatePost: UpdatePost = {
        type: "comment",
        comment: this.newComment.trim(),
        like: false,
        username: this.currentUser,
        title: '',
        content: '',
        categories: [],
        visibility: ''
      };
      
      this.postService.updatePost(this.post.id, updatePost).subscribe({
        next: (post) => {
          // console.log("new post",post)
          this.post = post;
         },
         error: (err) => {
          console.log(err);
         }
        });
      //todo: send to backend
      this.newComment = '';
    }
  }

// Add these to your component class
showComments: boolean = false;

// Toggle comments visibility
// toggleComments(): void {
//   this.showComments = !this.showComments;
// }

// Calculate read time
calculateReadTime(content: string): number {
  const wordsPerMinute = 200;
  const words = content.split(/\s+/).length;
  const minutes = Math.ceil(words / wordsPerMinute);
  return minutes || 1; // Minimum 1 minute
}

// Enhanced markdown rendering
renderMarkdown(text: string): void {
  if (!text) {
    this.renderedContent = '';
    return;
  }
  
  let html = text
    // Images
    .replace(/!\[(.*?)\]\((.*?)\)/gim, `
      <div class="text-center my-4">
        <img src="$2" alt="$1" class="img-fluid rounded shadow">
      </div>
    `)
    // Videos
    .replace(/Video URL:\s*(.*?\.mp4)/gim, `
      <div class="ratio ratio-16x9 my-4">
        <video controls class="rounded shadow">
          <source src="$1" type="video/mp4">
        </video>
      </div>
    `)
    // Headers
    .replace(/^### (.*$)/gim, '<h3 class="mt-4">$1</h3>')
    .replace(/^## (.*$)/gim, '<h2 class="mt-4">$1</h2>')
    .replace(/^# (.*$)/gim, '<h1 class="mt-4">$1</h1>')
    // Bold & Italic
    .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/gim, '<em>$1</em>')
    // Code
    .replace(/```([\s\S]*?)```/gim, '<pre class="bg-light p-3 rounded"><code>$1</code></pre>')
    .replace(/`(.*?)`/gim, '<code class="bg-light px-1 rounded">$1</code>')
    // Links
    .replace(/\[(.*?)\]\((.*?)\)/gim, '<a href="$2" class="text-primary">$1</a>')
    // Line breaks
    .replace(/\n/g, '<br>');
  
  this.renderedContent = this.sanitizer.bypassSecurityTrustHtml(html);
}

likeComment(comment: any) {
  console.log("hello",this.post)
   this.postService.likeComment(comment.id,this.currentUser).subscribe({
    next: (post) => {
      console.log("new post",post)
      this.post = post;
    },
    error: (err) => {
      console.log(err);
    }
   });
}

dislikeComment(comment: any){
  this.postService.dislikeComment(comment.id,this.currentUser).subscribe({
    next: (post) => {
      console.log("new post",post)
      this.post = post;
    },
    error: (err) => {
      console.log(err);
    }
   });
}

reportComment(comment: any) {

 this.postService.reportComment(comment.id,this.currentUser).subscribe({
  next: (message) => {
    alert(message);
    // console.log("lfdskfdskjlfkdskjfdsklf",message);
  },
  error: (err) => {
    console.log(err);
  }
 });
}

deleteComment(comment: any) {
  this.postService.deleteComment(comment.id).subscribe({
    next: (post) => {
      // console.log("new post",post)
      this.post.comments = this.post.comments.filter((c: any) => c.id !== comment.id);
    },
    error: (err) => {
      console.log(err);
    }
   });
}

deletePost() {
  this.postService.deletePost(this.post.id).subscribe({
    next: (message) => {
      alert(message);
      this.router.navigate(['/']);
    },
    error: (err) => {
      console.log(err);
    }
   });
}
}