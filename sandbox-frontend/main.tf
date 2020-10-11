provider "aws" {
  region = "us-west-2"
  profile = "default"
}

terraform {
  backend "remote" {
    organization = "waffelmonster"

    workspaces {
      name = "Sandbox"
    }
  }
}

locals {
  domain = "waffelmonster.com"
}

data "aws_iam_policy_document" "public" {
  statement {
    sid = "publicPermissions"
    principals {
      type = "*"
      identifiers = ["*"]
    }
    effect = "Allow"
    actions = ["s3:GetObject"]
    resources = [
      "arn:aws:s3:::${local.domain}",
      "arn:aws:s3:::${local.domain}/*"
    ]
  }
}

resource "aws_s3_bucket" "bucket" {
  bucket = local.domain
  acl    = "public-read"
  policy = data.aws_iam_policy_document.public.json

  lifecycle {
    prevent_destroy = true
  }

  website {
    index_document = "index.html"
    error_document = "index.html"
  }

  versioning {
    enabled = true
  }
}

resource "aws_cloudfront_distribution" "www_distribution" {
  enabled             = true
  default_root_object = "index.html"

  origin {
      domain_name = aws_s3_bucket.bucket.website_endpoint
      origin_id   = local.domain

      custom_origin_config {
        http_port              = "80"
        https_port             = "443"
        origin_protocol_policy = "http-only"
        origin_ssl_protocols   = ["TLSv1", "TLSv1.1", "TLSv1.2"]
      }
    }

  default_cache_behavior {
    viewer_protocol_policy = "redirect-to-https"
    compress               = true
    allowed_methods        = ["GET", "HEAD"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = local.domain
    # Setting cache to 0 ttl.
    # Barring use of a versioning strategy or having high traffic,
    # this makes deployment of new content easier by disabling caching.
    min_ttl                = 0
    default_ttl            = 0
    max_ttl                = 31536000

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }
  }

  aliases = [
    local.domain,
    "www.${local.domain}"
  ]

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn = "arn:aws:acm:us-east-1:038424901557:certificate/be832c74-da3c-4ab3-9e8d-6b6688236608"
    ssl_support_method  = "sni-only"
  }
}

resource "aws_route53_record" "www" {
  zone_id = "Z1I738FX85ZD94"
  name    = local.domain
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.www_distribution.domain_name
    zone_id                = aws_cloudfront_distribution.www_distribution.hosted_zone_id
    evaluate_target_health = false
  }
}
